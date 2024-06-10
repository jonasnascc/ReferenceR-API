package be.wanna.Referencerback.service.scraping;

import be.wanna.Referencerback.dto.AlbumDTO;
import be.wanna.Referencerback.dto.AuthorProfileDTO;
import be.wanna.Referencerback.dto.CsrfResponseDTO;
import be.wanna.Referencerback.dto.PhotoDTO;
import be.wanna.Referencerback.dto.deviantArt.TagDTO;
import be.wanna.Referencerback.dto.deviantArt.deviation.DeviationDTO;
import be.wanna.Referencerback.dto.deviantArt.deviation.mediaInfo.MediaDTO;
import be.wanna.Referencerback.dto.deviantArt.deviation.mediaInfo.mediatype.MediaTypeDTO;
import be.wanna.Referencerback.dto.deviantArt.deviation.mediaInfo.mediatype.Ss;
import be.wanna.Referencerback.dto.deviantArt.deviation.offset.OffSetDTO;
import be.wanna.Referencerback.dto.deviantArt.deviation.out.DeviationMediaDTO;
import be.wanna.Referencerback.dto.deviantArt.gallery.GalResultDTO;
import be.wanna.Referencerback.dto.deviantArt.gallery.GalleryInfoDTO;
import be.wanna.Referencerback.dto.deviantArt.gallery.ModuleDTO;
import be.wanna.Referencerback.dto.deviantArt.gallery.ProfileInfoDTO;
import be.wanna.Referencerback.entity.Author;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.photo.PhotoType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DeviantArtService {
    private final String DEVIANT_ART_URL = "https://www.deviantart.com/";

    private final String DEVIANTART = "deviantart";

    private final String OFFSET_URL = "https://www.deviantart.com/_puppy/dashared/gallection/contents";

    private final String USER_GALLERY_INFO_URL = "https://www.deviantart.com/_puppy/dauserprofile/init/gallery";

    private final String USER_PROFILE_INFO_URL = "https://www.deviantart.com/_puppy/dauserprofile/init/about";
    //username?
    //csrf_token?

    private final String DEVIATION_DETAILS_URL = "https://www.deviantart.com/_puppy/dadeviation/init";
    //?deviationid=1011484517&username=Nightvenjer&type=art&include_session=false&csrf_token=


    public List<AlbumDTO> findUserAlbums(String author){
        GalleryInfoDTO galInfo = getUserGalleryInfo(author);
        ModuleDTO moduleDTO = galInfo.gruser().page().modules().stream().filter(mod -> mod.name().equals("folders")).findAny().orElse(null);

        List<AlbumDTO> albums = new ArrayList<>();
        if(moduleDTO != null){
            List<GalResultDTO> results = moduleDTO.moduleData().folders().results();
            if(results!=null){
                results.forEach(res -> {
                    String id  = Integer.toString(res.folderId());
                    if(id.equals("-1")) id = "all";
                    else if(id.equals("-2")) id = "scraps";

                    String albumUrl = "https://www.deviantart.com/" + author + "/gallery/" + id;
                    albums.add(
                            getAlbum(
                                    id,
                                    res.name(),
                                    albumUrl,
                                    convertDeviationToPhotoDTO(getDeviation(res.thumb(), 300)),
                                    author,
                                    res.size()
                            )
                    );
                });
            }
        }
//
        return albums;
    }

    public Set<Photo> listAlbumPhotosByPage(String albumId, String authorName, int number, int limitByPage, Integer maxThumbsize){
        return getAlbumDeviations(albumId, authorName, number, limitByPage).stream()
                .map(dev -> getDeviation(dev, maxThumbsize)).collect(Collectors.toSet());
    }

    private Set<DeviationDTO> getAlbumDeviations(String albumId, String authorName, Integer page, Integer limitByPage){
        int offsetPg = (page - 1) * limitByPage;

        OffSetDTO offset = getOffSet(offsetPg, albumId, authorName, limitByPage);

        Set<DeviationDTO> setDeviations;
        if(offset!=null){
            setDeviations = new HashSet<>(offset.getResults());
        }
        else return Collections.emptySet();

        return setDeviations;
    }

    private GalleryInfoDTO getUserGalleryInfo(String authorName){
        CsrfResponseDTO csrfResponseDTO = getCsrfResponse(authorName);

        Map<String, String> params = new HashMap<>();
        params.put("username", authorName);
        params.put("deviations_limit", "1");
        params.put("with_subfolders", "true");
        params.put("csrf_token", csrfResponseDTO.csrfToken());
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("./testeUserProf.json"))){
            Connection.Response  response = Jsoup.connect(USER_GALLERY_INFO_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                    .referrer(DEVIATION_DETAILS_URL)
                    .data(params)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .cookies(csrfResponseDTO.cookies())
                    .execute();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            GalleryInfoDTO galleryInfoDTO = gson.fromJson(response.body(), GalleryInfoDTO.class);
            bw.write(gson.toJson(galleryInfoDTO));

            return galleryInfoDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AuthorProfileDTO getAuthorProfile(String authorName){
        ProfileInfoDTO profileInfo = getUsersProfileInfo(authorName);
        if(profileInfo==null) return null;

        return new AuthorProfileDTO(
                profileInfo.owner().usericon(),
                profileInfo.pageExtraData().gruserTagline(),
                profileInfo.owner().username(),
                profileInfo.pageExtraData().stats().deviations(),
                profileInfo.pageExtraData().stats().watchers(),
                profileInfo.pageExtraData().stats().watching(),
                profileInfo.pageExtraData().stats().pageviews(),
                profileInfo.pageExtraData().stats().favourites()
        );
    }

    private ProfileInfoDTO getUsersProfileInfo(String authorName){
        CsrfResponseDTO csrfResponseDTO = getCsrfResponse(authorName);

        Map<String, String> params = new HashMap<>();
        params.put("username", authorName);
        params.put("csrf_token", csrfResponseDTO.csrfToken());

        try(BufferedWriter bw = new BufferedWriter(new FileWriter("./testeUserProf.json"))){
            Connection.Response  response = Jsoup.connect(USER_PROFILE_INFO_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                    .referrer(DEVIATION_DETAILS_URL)
                    .data(params)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .cookies(csrfResponseDTO.cookies())
                    .execute();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            ProfileInfoDTO profileInfoDTO = gson.fromJson(response.body(), ProfileInfoDTO.class);
            bw.write(gson.toJson(profileInfoDTO));

            return profileInfoDTO;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TagDTO> getDeviationTagsByUrl(String deviationUrl) {
        try{
            Document doc = Jsoup.connect(deviationUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .get();

            List<Element> tagElements = doc.select("span._1nwad");

            List<TagDTO> tags = new ArrayList<>();
            if(tagElements.isEmpty()) return tags;

            tagElements.forEach(tag -> tags.add(new TagDTO(tag.text(), DEVIANT_ART_URL + "tag/" + tag.text())));

            return tags;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    private List<OffSetDTO> getAllOffSets(String albumId, String authorName){
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

    private OffSetDTO getOffSet(Integer number, String albumId, String authorName){
        return getOffSet(number, albumId, authorName, 60);
    }

    private OffSetDTO getOffSet(Integer number, String albumId, String authorName, Integer limit){
        CsrfResponseDTO csrfResponseDTO = getCsrfResponse(authorName);

        Document doc = null;
        try {
            Map<String, String> offSetParams = getOffSetParams(number, albumId, authorName, limit, csrfResponseDTO.csrfToken());

            doc = Jsoup.connect(OFFSET_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                    .referrer("https://www.deviantart.com/%s/gallery".formatted(authorName))
                    .data(offSetParams)
                    .method(org.jsoup.Connection.Method.GET)
                    .ignoreContentType(true)
                    .cookies(csrfResponseDTO.cookies())
                    .get();

        } catch (IOException e) {
            e.printStackTrace();
            //return getOffSet(number, album);
        }

        if(doc == null) {
            System.out.println();
            throw new RuntimeException("Error while readin' the page.");
        }
        Elements elements = doc.getElementsByTag("body");

        String json = elements.text();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try{
            return gson.fromJson(json, OffSetDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    private CsrfResponseDTO getCsrfResponse(String authorName) {
        try {
            org.jsoup.Connection.Response response = Jsoup.connect("https://www.deviantart.com/%s/gallery".formatted(authorName))
                    .method(org.jsoup.Connection.Method.GET)
                    .execute();

            String startOfLine = response.body().substring(response.body().indexOf("__CSRF_TOKEN__"));
            String tokenLine = startOfLine.substring(0, startOfLine.indexOf(";"));

            return new CsrfResponseDTO(tokenLine.split("'")[1], response.cookies());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> getOffSetParams(Integer number, String albumId, String authorName, Integer limit, String csrfToken){
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

    }

    private PhotoDTO convertDeviationToPhotoDTO(Photo deviation) {
        return new PhotoDTO(deviation.getId(), deviation.getCode(), deviation.getUrl(), deviation.getTitle(), deviation.isMature());
    }

    public DeviationMediaDTO getDeviationInfoByUrl(String url){
        try{
            Map<String, String> data = new HashMap<>();
            data.put("url", url);

            Connection.Response response = Jsoup.connect("https://backend.deviantart.com/oembed")
                    .data(data)
                    .ignoreContentType(true)
                    .execute();

            Gson gson = new Gson().newBuilder().setPrettyPrinting().create();
            return gson.fromJson(response.body(), DeviationMediaDTO.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getAlbumIdStr(String albumId){
        return albumId.replace(DEVIANTART.concat("-"), "");
    }

    private AlbumDTO getAlbum(String id, String name, String url, PhotoDTO thumbnail, String author, Integer photosNum){
        return new AlbumDTO(null ,id, name, url, thumbnail, author, DEVIANTART, photosNum, false);
    }

    public void findUserAlbumsByPageDocument(String author){
//        System.out.println("Getting gallery albums...");
        /*String url = getAuthorProfileUrl(author).concat("/gallery");
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
                album = getAlbum(albumId, albumName, albumUrl, "thumbUrl", author, photosNum);
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
        return albums;*/
    }

    private Photo getDeviation(DeviationDTO dto, Integer maxThumbsize){
        Author author = new Author();
        author.setName(dto.getAuthor().getUsername());
        author.setProfileUrl(DEVIANT_ART_URL + author.getName());

        Photo deviation = new Photo();
        deviation.setCode(Long.toString(dto.getDeviationId()));
        deviation.setPhotoPage(dto.getUrl());
        deviation.setTitle(dto.getTitle());
        deviation.setMature(dto.isMature());
        deviation.setMatureLevel(dto.getMatureLevel());
        deviation.setLicense(dto.getLicense());
        deviation.setType(PhotoType.DEVIATION);

        deviation.setUrl(getDeviationDownloadUrl(dto));
        if(maxThumbsize!=null) deviation.setThumbUrl(getDeviationDownloadUrl(dto, maxThumbsize));

        return deviation;
    }

    private String getDeviationDownloadUrl(DeviationDTO dto) {
        return getDeviationDownloadUrlByType(dto.getMedia());
    }

    private String getDeviationDownloadUrl(DeviationDTO dto, int maxThumbSize) {
        return getDeviationDownloadUrlByType(dto.getMedia(), maxThumbSize);
    }

    private String getDeviationDownloadUrlByType(MediaDTO media){
        return getDeviationDownloadUrlIncr(media, null, true);
    }

    private String getDeviationDownloadUrlByType(MediaDTO media, int maxHeight){
        return getDeviationDownloadUrlIncr(media,  maxHeight, false);
    }

    private String getDeviationDownloadUrlIncr (MediaDTO media, Integer maxHeight, boolean fullSize) {
        if(media.getBaseUri()==null || (maxHeight==null && !fullSize)) return "";

        boolean fullHeight = false;
        if(fullSize) fullHeight = fullSize;

        List<MediaTypeDTO> types = media.getTypes();

        int bestIndex = 0;
        for (int i = 1; i < types.size(); i++) {
            MediaTypeDTO currentType = validateType(types.get(i));
            MediaTypeDTO best = validateType(types.get(bestIndex));
            if( this.checkValidTypeDto(best) && this.checkValidTypeDto(currentType) ) {
                int typeHeight = currentType.getH();
                if (typeHeight > best.getH()) {
                    if(!fullHeight && typeHeight <= maxHeight){
                        bestIndex = i;
                    }
                    else if(fullHeight) {
                        bestIndex = i;
                    }
                }
            } else {
                bestIndex = i;
            }
        }
        if(!this.checkValidTypeDto(types.get(bestIndex))) return "";

        MediaTypeDTO type = types.get(bestIndex);
        String view = type.getC()==null ? "" : type.getC();
        String prettyName = media.getPrettyName();
        String token = media.getToken() != null ? ("?token=" + media.getToken().get(0)) : "";

        return media.getBaseUri().concat(view + token).replace("<prettyName>", prettyName);
    }

    private MediaTypeDTO validateType (MediaTypeDTO type) {
        if(!checkValidTypeDto(type)) {
            List<Ss> ssList = type.getSs();
            if(ssList == null ) return type;
            if(!ssList.isEmpty()){
                Ss ss = ssList.get(0);

                if(ss.getH() == null || ss.getW() == null) return type;
                type.setW(ss.getW());
                type.setH(ss.getH());
                type.setC(ss.getC());
            }
        }
        return type;
    }

    private boolean checkValidTypeDto (MediaTypeDTO type) {
        return type.getH()!=null || type.getW()!=null;
    }

    private String getAuthorProfileUrl(String author){
        return DEVIANT_ART_URL.concat(author);
    }

    public PhotoDTO getDeviationWithToken(Photo deviation, String author) {
        String url = "https://www.deviantart.com/"
                +author+
                "/art/"
                +deviation.getTitle().replace(" " , "-") + "-" + deviation.getCode();
        deviation.setPhotoPage(url);

        DeviationMediaDTO infoByUrl = getDeviationInfoByUrl(deviation.getPhotoPage());

        return new PhotoDTO(
                deviation.getId(),
                deviation.getCode(),
                infoByUrl.thumbnail_url(),
                deviation.getTitle(),
                deviation.isMature()
        );
    }
}
