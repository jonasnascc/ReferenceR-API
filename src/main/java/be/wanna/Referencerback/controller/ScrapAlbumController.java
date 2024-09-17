package be.wanna.Referencerback.controller;

import be.wanna.Referencerback.service.album.ScrapAlbumService;
import be.wanna.Referencerback.service.authorization.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScrapAlbumController {
    public final ScrapAlbumService service;

    public final TokenService tokenService;

    @GetMapping(value = "author/{author}/albums", produces = "application/json")
    public ResponseEntity<?> listAuthorAlbums(
            @RequestHeader("Resources-provider") String provider,
            @PathVariable String author
    ){
        return ResponseEntity.ok(service.getAuthorAlbums(author, provider));
    }

    @GetMapping("albums/{albumId}/thumbnail")
    public ResponseEntity<?> getAlbumThumbnail (
            @PathVariable Long albumId
    ) {
        return ResponseEntity.ok(service.getAlbumThumbnail(albumId));
    }
}
