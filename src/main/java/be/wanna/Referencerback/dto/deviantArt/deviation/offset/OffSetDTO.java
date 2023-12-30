package be.wanna.Referencerback.dto.deviantArt.deviation.offset;

import be.wanna.Referencerback.dto.deviantArt.deviation.DeviationDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OffSetDTO {
    private boolean hasMore;
    private int nextOffset;
    private List<DeviationDTO> results;

    public boolean hasMore(){
        return hasMore;
    }

    @Override
    public String toString() {
        return "OffSetDTO{" +
                "hasMore=" + hasMore +
                ", nextOffset=" + nextOffset +
                ", resultDTOS=" + results +
                '}';
    }
}
