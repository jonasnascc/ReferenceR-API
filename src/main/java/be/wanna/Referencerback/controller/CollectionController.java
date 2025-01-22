package be.wanna.Referencerback.controller;

import be.wanna.Referencerback.dto.IdsListDTO;
import be.wanna.Referencerback.dto.userCollection.CollectionDTOIn;
import be.wanna.Referencerback.dto.userCollection.CollectionPhotosDTO;
import be.wanna.Referencerback.service.authorization.TokenService;
import be.wanna.Referencerback.service.collections.CollectionsService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/collections")
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionsService service;

    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CollectionDTOIn dto
    ) {
        String login = tokenService.validateToken(authorization);

        return ResponseEntity.ok(service.create(login, dto));
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestHeader("Authorization") String authorization,
            @PathParam("asAlbums") Boolean asAlbums
    ) {
        String login = tokenService.validateToken(authorization);
        if(asAlbums!=null && asAlbums) return ResponseEntity.ok(service.listAsAlbums(login));

        return ResponseEntity.ok(service.list(login));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> find(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id
    ) {
        String login = tokenService.validateToken(authorization);

        return ResponseEntity.ok(service.find(login, id));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> update(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestBody CollectionDTOIn dto
    ) {
        String login = tokenService.validateToken(authorization);

        return ResponseEntity.ok(service.update(login, id, dto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id
    ) {
        String login = tokenService.validateToken(authorization);

        service.delete(login, id);

        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "{id}/photos" , produces = "application/json")
    public ResponseEntity<?> addPhotos(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestBody CollectionPhotosDTO dto

            ) {
        String login = tokenService.validateToken(authorization);

        return ResponseEntity.ok(service.addPhotos(login, id, dto));
    }

    @GetMapping("{id}/albums")
    public ResponseEntity<?> listAlbums(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id
    ) {
        String login = tokenService.validateToken(authorization);


        return ResponseEntity.ok(service.listAlbums(login, id));
    }

    @GetMapping("{id}/albums/{albumId}/photos")
    public ResponseEntity<?> listAlbumPhotos(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @PathVariable Long albumId
    ) {
        String login = tokenService.validateToken(authorization);


        return ResponseEntity.ok(service.listAlbumPhotos(login, id, albumId));
    }

    @GetMapping("{id}/photos")
    public ResponseEntity<?> listPhotos(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @PathParam("page") Integer page,
            @PathParam("limit") Integer limit
    ) {
        String login = tokenService.validateToken(authorization);

        return ResponseEntity.ok(service.listPhotos(login, id, page, limit));
    }

    @DeleteMapping("{id}/photos/{photoId}")
    public ResponseEntity<?> deletePhoto(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @PathVariable Long photoId
    ) {
        String login = tokenService.validateToken(authorization);

        service.deletePhoto(login, id, photoId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}/photos")
    public ResponseEntity<?> deletePhotos(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestBody IdsListDTO dto
    ) {
        String login = tokenService.validateToken(authorization);

        service.deletePhotos(login, id, dto.ids());

        return ResponseEntity.ok().build();
    }

    @GetMapping("{id}/thumbnail")
    public ResponseEntity<?> getThumbnail(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id
    ) {
        String login = tokenService.validateToken(authorization);

        return ResponseEntity.ok(service.getCollectionThumbnail(login, id));
    }
}
