package be.wanna.Referencerback.dto.deviantArt.deviation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviationAlbumDTO {
    private Long id;

    private String name;

    private String url;

    private Set<DeviationDTO> deviations;
}
