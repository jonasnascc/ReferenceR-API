package be.wanna.Referencerback.controller;

import be.wanna.Referencerback.service.author.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/author/")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService service;

    @GetMapping(value = "{authorName}", produces = {"application/json"})
    public ResponseEntity<?> getAuthor(
            @RequestHeader("Resources-provider") String provider,
            @PathVariable String authorName
    ){
        return ResponseEntity.ok(service.getAuthorProfile(authorName));
    }
}
