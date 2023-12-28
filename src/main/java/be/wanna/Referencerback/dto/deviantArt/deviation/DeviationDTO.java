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

    private boolean isMature;

    private String matureLevel;

    private DeviantAuthorDTO author;

    private String downloadPage;

    private MediaDTO media;


    @Override
    public String toString() {
        return "Deviation{" +
                "deviationId=" + deviationId +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", matureLevel='" + matureLevel + '\'' +
                ", author=" + author +
                ", downloadPage='" + downloadPage + '\'' +
                ", media=" + media +
                '}';
    }
}
