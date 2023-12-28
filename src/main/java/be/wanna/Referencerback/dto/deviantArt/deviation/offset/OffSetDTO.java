package be.wanna.Referencerback.dto.deviantArt.deviation.offset;

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
    private List<ResultDTO> results;

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
