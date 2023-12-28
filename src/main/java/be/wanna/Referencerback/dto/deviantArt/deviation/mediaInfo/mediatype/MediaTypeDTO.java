package be.wanna.Referencerback.dto.deviantArt.deviation.mediaInfo.mediatype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaTypeDTO {
    private String t;
    private Integer r;
    private String c;
    private Integer h;
    private Integer w;
    private ArrayList<Ss> ss;

    @Override
    public String toString() {
        return "MediaTypeDTO{" +
                "t='" + t + '\'' +
                ", r=" + r +
                ", c='" + c + '\'' +
                ", h=" + h +
                ", w=" + w +
                ", ss=" + ss +
                '}';
    }
}


