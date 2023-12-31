package be.wanna.Referencerback.dto.deviantArt.deviation.out;

public record DeviationMediaDTO (
        String type,
        String title,
        String category,
        String url,
        String author_name,
        String author_url,
        String provider_name,
        String safety,
        String pubdate,
        String rating,
        String tags,
        Integer width,
        Integer height,
        String imagetype,
        String thumbnail_url,
        String thumbnail_width,
        String thumbnail_height,
        String thumbnail_url_150,
        String thumbnail_url_200h,
        String thumbnail_width_200h,
        String thumbnail_height_200h,

        CopyrightDTO copyright
){}
