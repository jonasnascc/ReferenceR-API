package be.wanna.Referencerback.dto.deviantArt.deviation.mediaInfo.mediatype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class Ss {
    private Integer x;
    private String c;

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    @Override
    public String toString() {
        return "Ss{" +
                "x=" + x +
                ", c='" + c + '\'' +
                '}';
    }
}
