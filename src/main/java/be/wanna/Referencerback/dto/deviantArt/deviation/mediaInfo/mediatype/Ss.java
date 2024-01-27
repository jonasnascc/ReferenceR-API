package be.wanna.Referencerback.dto.deviantArt.deviation.mediaInfo.mediatype;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Ss {
    private Integer x;
    private String c;
    private Integer h;
    private Integer w;

    @Override
    public String toString() {
        return "Ss{" +
                "x=" + x +
                ", c='" + c + '\'' +
                ", h=" + h +
                ", w=" + w +
                '}';
    }
}
