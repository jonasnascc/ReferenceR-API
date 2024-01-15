package be.wanna.Referencerback.dto.deviantArt.gallery;

import be.wanna.Referencerback.dto.deviantArt.deviation.DeviationDTO;


public record ModuleDataDTO(
    String dataKey,
    FolderDTO folders,
    GalResultDTO folderDeviations,

    DeviationDTO coverDeviation
) {}
