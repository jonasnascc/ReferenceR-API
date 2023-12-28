package be.wanna.Referencerback.dto.deviantArt.deviation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviantAuthorDTO {
    private Long userId;

    private String username;
}
