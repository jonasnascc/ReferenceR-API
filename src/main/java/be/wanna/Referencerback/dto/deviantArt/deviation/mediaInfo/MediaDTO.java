package be.wanna.Referencerback.dto.deviantArt.deviation.mediaInfo;
import be.wanna.Referencerback.dto.deviantArt.deviation.mediaInfo.mediatype.MediaTypeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaDTO {
    private String baseUri;
    private String prettyName;
    private List<String> token;
    private List<MediaTypeDTO> types;

    @Override
    public String toString() {
        return "Media{" +
                "baseUri='" + baseUri + '\'' +
                ", prettyName='" + prettyName + '\'' +
                ", token=" + token +
                ", types=" + types +
                '}';
    }
}
