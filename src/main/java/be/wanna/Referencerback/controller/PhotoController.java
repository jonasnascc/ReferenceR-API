package be.wanna.Referencerback.controller;

import be.wanna.Referencerback.dto.PhotoDTO;
import be.wanna.Referencerback.service.album.AlbumsService;
import be.wanna.Referencerback.service.authorization.TokenService;
import be.wanna.Referencerback.service.photo.PhotoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/author")
@AllArgsConstructor
public class PhotoController {
    private final AlbumsService albumService;

    private final PhotoService photoService;

    private final TokenService tokenService;
    @GetMapping(value = "{author}/albums/{albumCode}/photos" )
    public ResponseEntity<?> getAlbumPhotos(
            @RequestHeader("Resources-provider") String provider,
            @RequestParam Integer page,
            @RequestParam Integer limit,
            @RequestParam Integer maxThumbSize,
            @PathVariable String author,
            @PathVariable String albumCode
    ){
        return new ResponseEntity<>(albumService.listPhotos(author, albumCode, page, limit, provider, maxThumbSize), HttpStatus.OK);
    }

    @PostMapping("albums/favorites/{albumId}/photos")
    public ResponseEntity<?> favorite (
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("Resources-provider") String provider,
            @RequestBody PhotoDTO dto,
            @PathVariable Long albumId
    ) {
        return ResponseEntity.ok(photoService.favoritePhoto(dto, albumId, tokenService.validateToken(authorization)));
    }

    @DeleteMapping("albums/favorites/{albumId}/photos/{id}")
    public ResponseEntity<?> unfavorite (
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("Resources-provider") String provider,
            @PathVariable Long albumId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(photoService.unfavoritePhoto(id,null, albumId, tokenService.validateToken(authorization)));
    }



}
