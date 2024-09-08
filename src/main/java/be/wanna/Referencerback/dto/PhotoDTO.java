package be.wanna.Referencerback.dto;

public record PhotoDTO (
        Long id,
        String code,
        String url,
        String title,
        Integer page,
        String albumCode,
        Boolean mature
) {
    public PhotoDTO (
            Long id,
            String code,
            String url,
            String title,
            Boolean mature
    ){
        this(id, code, url, title, null, null, mature);
    }

    public PhotoDTO (
            String code,
            Integer page
    ){
        this(null, code, null, null, page, null, null);
    }
}
