package be.wanna.Referencerback.controller;

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



}
