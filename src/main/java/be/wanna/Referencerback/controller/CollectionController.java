package be.wanna.Referencerback.controller;

import be.wanna.Referencerback.dto.CollectionDTOIn;
import be.wanna.Referencerback.service.authorization.TokenService;
import be.wanna.Referencerback.service.collections.CollectionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionsService service;

    private final TokenService tokenService;

    @PostMapping("user/collections")
    public ResponseEntity<?> create(
            @RequestHeader("Authorization") String authorization,
            @RequestBody CollectionDTOIn dto
    ) {
        String login = tokenService.validateToken(authorization);

        return ResponseEntity.ok(service.create(login, dto));
    }
}
