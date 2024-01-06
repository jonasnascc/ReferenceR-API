package be.wanna.Referencerback.service.scraping;

import be.wanna.Referencerback.dto.AlbumDTO;
import be.wanna.Referencerback.dto.deviantArt.deviation.DeviationAlbumDTO;
import be.wanna.Referencerback.dto.deviantArt.deviation.DeviationDTO;
import be.wanna.Referencerback.dto.deviantArt.deviation.offset.OffSetDTO;
import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.photo.Deviation;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.User;
import be.wanna.Referencerback.entity.photo.PhotoType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public abstract class DeviantArtService {
    private final static String DEVIANT_ART_URL = "https://www.deviantart.com/";

    private final static String DEVIANTART = "deviantart";

    private final static String OFFSET_URL = "https://www.deviantart.com/_puppy/dashared/gallection/contents";

    public static List<AlbumDTO> findUserAlbums(String author){
//        System.out.println("Getting gallery albums...");
        String url = getAuthorProfileUrl(author).concat("/gallery");
//
        Document doc = null;
        try{
            doc = Jsoup.connect(url).ignoreContentType(true).get();
        } catch (IOException e){
            System.out.printf("Erro na conexão com o perfil de usuário Deviantart: " + e.getMessage());
        }

        assert doc != null;
        Elements elements = doc.getElementsByTag("section").select("._1olSA._3zHuD");
        System.out.println(elements.size());

        List<AlbumDTO> albums = new ArrayList<>();
        for(Element e : elements){
            String albumName = e.getElementsByTag("h2").select("._1nhB_").attr("title");
            String albumUrl = e.getElementsByTag("a").attr("href");
            String thumbUrl = e.getElementsByTag("div").select("._24Wda > img").attr("src");

            Optional<Element> optDeviationsNum = e.getElementsByTag("span").select("._3XJHo")
                    .stream().toList()
                    .stream().filter(el -> el.text().toLowerCase().contains("deviations"))
                    .findFirst();

            Integer photosNum = null;
            try{
                String devNum = "";
                if(optDeviationsNum.isPresent()){
                    devNum = optDeviationsNum.get().text();
                }
                photosNum = Integer.parseInt(devNum.replace(" deviations", "").trim());
            } catch (Exception ignored){}

            DeviationAlbumDTO devAlbum = null;
            AlbumDTO album = null;
            try {
                String albumId = albumUrl.split("/")[5];
                album = getAlbum(albumId, albumName, albumUrl, thumbUrl, author, photosNum);
            } catch (Exception err) {
                System.out.printf("Erro ao obter álbum: " + err.getMessage());
            }

            if(album != null) {
                //album.setPhotos(getAlbumDeviations(album).stream().map(DvArtScrap::getPhoto).collect(Collectors.toList()));
                //devAlbum.setDeviations(getAlbumDeviations(album));
                albums.add(album);
                System.out.println("- " + album.name() + " (" + " photos) added.");
            }
        }
//
        return albums;
    }

    public static Set<Photo> listAlbumPhotosByPage(String albumId, String authorName, int number, int limitByPage, User user){
        return getAlbumDeviations(albumId, authorName, number, limitByPage, user).stream().map(DeviantArtService::getDeviation).collect(Collectors.toSet());
    }

    private static Set<DeviationDTO> getAlbumDeviations(String albumId, String authorName, Integer page, Integer limitByPage, User user){
        OffSetDTO offset = getOffSet(page, albumId, authorName, limitByPage);

        Set<DeviationDTO> listDevs;
        if(offset!=null){
            listDevs = new HashSet<>(offset.getResults());
        }
        else return Collections.emptySet();

        return listDevs;
    }

    private static List<OffSetDTO> getAllOffSets(String albumId, String authorName){
        List<OffSetDTO> list = new ArrayList<>();

        OffSetDTO offSet = getOffSet(0, albumId, authorName);
        list.add(offSet);

        System.out.println("Gettin' OffSets...");
        int count = 0;

        while(offSet!=null && offSet.hasMore()){

            System.out.print(count + " -> ");
            offSet = getOffSet(offSet.getNextOffset(), albumId, authorName);
            if(offSet != null) list.add(offSet);
            count++;

        }

        return list;
    }

    private static OffSetDTO getOffSet(Integer number, String albumId, String authorName){
        return getOffSet(number, albumId, authorName, 60);
    }

    private static OffSetDTO getOffSet(Integer number, String albumId, String authorName, Integer limit){
        org.jsoup.Connection.Response response;
        String csrfToken;
        try {
            response = Jsoup.connect("https://www.deviantart.com/%s/gallery".formatted(authorName))
                    .method(org.jsoup.Connection.Method.GET)
                    .execute();

            String startOfLine = response.body().substring(response.body().indexOf("__CSRF_TOKEN__"));
            String tokenLine = startOfLine.substring(0, startOfLine.indexOf(";"));
            csrfToken = tokenLine.split("'")[1];

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Document doc = null;
        try {
            Map<String, String> offSetParams = getOffSetParams(number, albumId, authorName, limit, csrfToken);

            doc = Jsoup.connect(OFFSET_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                    .referrer("https://www.deviantart.com/%s/gallery".formatted(authorName))
                    .data(offSetParams)
                    .method(org.jsoup.Connection.Method.GET)
                    .ignoreContentType(true)
                    .cookies(response.cookies())
                    .get();

        } catch (IOException e) {
            e.printStackTrace();
            //return getOffSet(number, album);
        }

        if(doc == null) throw new RuntimeException("Error while readin' the page.");
        Elements elements = doc.getElementsByTag("body");

        String json = elements.text();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try{
            return gson.fromJson(json, OffSetDTO.class);
        } catch (Exception e) {
            return null;
        }
    }
    private static Map<String, String> getOffSetParams(Integer number, String albumId, String authorName, Integer limit, String csrfToken){
        Map<String, String> params = new HashMap<>();

        params.put("username", authorName);
        params.put("type", "gallery");
        params.put("offset", Integer.toString(number));
        params.put("limit", Integer.toString(limit));

        if(albumId.contains(authorName))
            if(authorName.equals("All")){
                params.put("all_folder", "true");
            } else return Collections.emptyMap();
        else params.put("folderid", getAlbumIdStr(albumId));

        params.put("csrf_token", csrfToken);

        return params;


//        if(albumId.contains(authorName))
//            if(authorName.equals("All"))
//                return ("https://www.deviantart.com/_puppy/dashared/gallection/contents" +
//                        "username=AdorkaStock&type=gallery" +
//                        "&offset=24" +
//                        "&limit=24" +
//                        "&all_folder=true" +
//                        "&csrf_token=%s")
//                        .formatted(authorName, number, limit, csrfToken);
//            else  return null;
//        else return("https://www.deviantart.com/_puppy/dashared/gallection/contents" +
//                "?username=%s" +
//                "&type=gallery" +
//                "&offset=%d" +
//                "&limit=%d" +
//                "&folderid=%s" +
//                "&csrf_token=%s")
//                .formatted(authorName, number, limit, getAlbumIdStr(albumId), csrfToken);
    }
//    private static String getOffSetUrl(Integer number, String albumId, String authorName, Integer limit, String csrfToken){
//        if(albumId.contains(authorName))
//            if(authorName.equals("All"))
//                return ("https://www.deviantart.com/_puppy/dashared/gallection/contents" +
//                        "username=AdorkaStock&type=gallery" +
//                        "&offset=24" +
//                        "&limit=24" +
//                        "&all_folder=true" +
//                        "&csrf_token=%s")
//                        .formatted(authorName, number, limit, csrfToken);
//            else  return null;
//        else return("https://www.deviantart.com/_puppy/dashared/gallection/contents" +
//                    "?username=%s" +
//                    "&type=gallery" +
//                    "&offset=%d" +
//                    "&limit=%d" +
//                    "&folderid=%s" +
//                    "&csrf_token=%s")
//                .formatted(authorName, number, limit, getAlbumIdStr(albumId), csrfToken);
//    }

    private static String getAlbumIdStr(String albumId){
        return albumId.replace(DEVIANTART.concat("-"), "");
    }

    private static AlbumDTO getAlbum(String id, String name, String url,  String thumbUrl, String author, Integer photosNum){
        String code;
        if(id.equals("all")){
            code = DEVIANTART.concat("-").concat(author);
        } else
            code = DEVIANTART.concat("-").concat(id);

        return new AlbumDTO(code, name, url, thumbUrl, author, DEVIANTART, photosNum);
    }

    private static Deviation getDeviation(DeviationDTO dto){
        Author author = new Author();
        author.setName(dto.getAuthor().getUsername());
        author.setProfileUrl(DEVIANT_ART_URL + author.getName());

        Deviation deviation = new Deviation();
        deviation.setId(dto.getDeviationId());
        deviation.setDeviationPage(dto.getUrl());
        deviation.setTitle(dto.getTitle());
        deviation.setMature(dto.isMature());
        deviation.setMatureLevel(dto.getMatureLevel());
        deviation.setLicense(dto.getLicense());
        deviation.setType(PhotoType.DEVIATION);

//        String url = dto.getMedia().getBaseUri();
//        try{
//            String prettyName = dto.getMedia().getPrettyName();
//            Optional<MediaTypeDTO> mediaType = dto.getMedia().getTypes().stream().filter(type -> type.getT().contains("fullview")).findAny();
//            if(mediaType.isPresent()){
//                url = url.concat( mediaType.get().getC().replace("<prettyName>", prettyName) );
//            }
//            deviation.setUrl(url);
//        } catch (NullPointerException ignored){}

        return deviation;
    }
    private static String getAuthorProfileUrl(String author){
        return DEVIANT_ART_URL.concat(author);
    }
}
