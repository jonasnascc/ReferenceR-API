package be.wanna.Referencerback.controller;

import be.wanna.Referencerback.dto.AlbumDTO;
import be.wanna.Referencerback.service.album.AlbumsService;
import be.wanna.Referencerback.service.authorization.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AlbumController {
    public final AlbumsService service;

    public final TokenService tokenService;

    @GetMapping(value = "author/{author}/albums", produces = "application/json")
    public ResponseEntity<?> listAuthorAlbums(
            @RequestParam String provider,
            @PathVariable String author
    ){
        return ResponseEntity.ok(service.getAuthorAlbums(author, provider));
    }

    @PostMapping("albums/favorite")
    public ResponseEntity<?> favoriteAlbum(
            @RequestHeader("Authorization") String authorization,
            @RequestBody AlbumDTO dto
    ) {
        String login = tokenService.validateToken(authorization);

        return ResponseEntity.ok(service.favoriteAlbum(dto, login));
    }

    @DeleteMapping("albums/favorites/{id}/unfavorite")
    public ResponseEntity<?> unFavoriteAlbum(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id
    ) {
        String login = tokenService.validateToken(authorization);

        return ResponseEntity.ok( service.unFavoriteAlbum(id, login));
    }

    @DeleteMapping("author/{author}/albums/{code}")
    public ResponseEntity<?> unFavoriteAlbumByUnsaved(
            @RequestHeader("Authorization") String authorization,
            @RequestParam String provider,
            @PathVariable String author,
            @PathVariable String code

    ) {
        String login = tokenService.validateToken(authorization);

        return ResponseEntity.ok( service.unFavoriteAlbum(code, author, provider, login));
    }

    @GetMapping("albums/favorites")
    public ResponseEntity<?> getFavoriteAlbums(
            @RequestHeader("Authorization") String authorization
    ) {
        String login = tokenService.validateToken(authorization);

        return ResponseEntity.ok( service.getFavoritedAlbums(login) );
    }
}
