package be.wanna.Referencerback.dto.deviantArt.deviation;

import be.wanna.Referencerback.dto.deviantArt.deviation.mediaInfo.MediaDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviationDTO {

    private long deviationId;

    private String title;

    private String type;

    private String url;

    private String thumbUrl;

    private boolean isMature;

    private String matureLevel;

    private String license;

    private DeviantAuthorDTO author;

    private MediaDTO media;

    private String publishedTime;

    private Integer page;


    @Override
    public String toString() {
        return "Deviation{" +
                "deviationId=" + deviationId +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", matureLevel='" + matureLevel + '\'' +
                ", author=" + author +
                ", media=" + media +
                '}';
    }
}
