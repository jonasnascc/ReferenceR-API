package be.wanna.Referencerback.dto.deviantArt.gallery;

import be.wanna.Referencerback.dto.deviantArt.deviation.DeviationDTO;

public record GalResultDTO (
        Integer folderId,
        String type,
        String name,
        String description,
        Integer size,

        DeviationDTO thumb
){}
