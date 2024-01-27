package be.wanna.Referencerback.service;

import be.wanna.Referencerback.dto.deviantArt.TagDTO;
import be.wanna.Referencerback.service.scraping.DeviantArtService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final DeviantArtService deviantArtService;

    public String getDeviationInfoByUrl(String url){
        try{
            Map<String, String> data = new HashMap<>();
            data.put("url", url);

            Connection.Response response = Jsoup.connect("https://backend.deviantart.com/oembed")
                    .data(data)
                    .ignoreContentType(true)
                    .execute();

            return response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TagDTO> getTagsByUrl(String url) {
        return deviantArtService.getDeviationTagsByUrl(url);
    }
}
