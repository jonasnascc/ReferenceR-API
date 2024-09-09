package be.wanna.Referencerback.controller;

import be.wanna.Referencerback.dto.userCollection.AlbumCollectionDTO;
import be.wanna.Referencerback.dto.userCollection.CollectionDTOIn;
import be.wanna.Referencerback.dto.userCollection.CollectionPhotosDTO;
import be.wanna.Referencerback.service.authorization.TokenService;
import be.wanna.Referencerback.service.collections.CollectionsService;
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
            @RequestHeader("Authorization") String authorization
    ) {
        String login = tokenService.validateToken(authorization);

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

        service.addPhotos(login, id, dto);

        return ResponseEntity.ok(null);
    }

    @GetMapping("{id}/photos")
    public ResponseEntity<?> listPhotos(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id
    ) {
        String login = tokenService.validateToken(authorization);


        return ResponseEntity.ok(service.listPhotos(login, id));
    }
}
