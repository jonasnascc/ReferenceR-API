package be.wanna.Referencerback.dto.photo;

public record PhotoAlbumByPageDTO(
   Long id,
   Integer page,
   Long albumId,
   String albumCode
) {
    public PhotoAlbumByPageDTO(Long id, Integer page, Long albumId) {
        this(id, page, albumId, null);
    }

    public PhotoAlbumByPageDTO(Integer page, Long albumId) {
        this(null, page, albumId, null);
    }

    public PhotoAlbumByPageDTO(Integer page, String albumCode) {
        this(null, page, null, albumCode);
    }
}
