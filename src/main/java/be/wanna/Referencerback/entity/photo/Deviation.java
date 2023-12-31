package be.wanna.Referencerback.entity.photo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Deviation extends Photo{
    private String matureLevel;

    private String deviationPage;

    private String license;
}
