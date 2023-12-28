package be.wanna.Referencerback.service.scraping;

import be.wanna.Referencerback.dto.AlbumDTO;
import be.wanna.Referencerback.dto.deviantArt.deviation.DeviationAlbumDTO;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public abstract class DeviantArtService {

    private final static String deviantArtUrl = "https://www.deviantart.com/";

    private final static String DEVIANTART = "deviantart";

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
                album = getAlbum(albumId, albumName, albumUrl, author, photosNum);
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

    private static AlbumDTO getAlbum(String id, String name, String url, String author, Integer photosNum){
        String code;
        if(id.equals("all")){
            code = DEVIANTART.concat("-").concat(author);
        } else
            code = DEVIANTART.concat("-").concat(id);

        return new AlbumDTO(code, name, url, author, DEVIANTART, photosNum);
    }

    private static String getAuthorProfileUrl(String author){
        return deviantArtUrl.concat(author);
    }
}
