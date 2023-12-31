package be.wanna.Referencerback.service;

import be.wanna.Referencerback.dto.deviantArt.deviation.out.DeviationMediaDTO;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PhotoService {
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
}
