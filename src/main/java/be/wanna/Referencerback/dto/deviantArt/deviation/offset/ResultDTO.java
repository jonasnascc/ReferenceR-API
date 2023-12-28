package be.wanna.Referencerback.dto.deviantArt.deviation.offset;

import be.wanna.Referencerback.dto.deviantArt.deviation.DeviationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO {
    private DeviationDTO deviation;

    public DeviationDTO getDeviation() {
        return deviation;
    }

    public void setDeviation(DeviationDTO deviationDTO) {
        this.deviation = deviationDTO;
    }

    @Override
    public String toString() {
        return "Result{" +
                "deviation=" + deviation +
                '}';
    }
}
