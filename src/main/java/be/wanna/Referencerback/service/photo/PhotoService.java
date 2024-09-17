package be.wanna.Referencerback.service.photo;

import be.wanna.Referencerback.dto.PhotoDTO;
import be.wanna.Referencerback.dto.deviantArt.TagDTO;
import be.wanna.Referencerback.entity.photo.Photo;
import be.wanna.Referencerback.entity.user.User;
import be.wanna.Referencerback.repository.PhotoRepository;
import be.wanna.Referencerback.repository.UserRepository;
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

    private final PhotoRepository photoRepository;

    private final UserRepository userRepository;

    public Long save(Photo photo){

        return photoRepository.save(photo).getId();
    }

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
