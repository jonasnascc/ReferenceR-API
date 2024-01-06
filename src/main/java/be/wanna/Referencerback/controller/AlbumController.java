package be.wanna.Referencerback.controller;

import be.wanna.Referencerback.service.AlbumsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/author/")
@RequiredArgsConstructor
public class AlbumController {
    public final AlbumsService service;

    @GetMapping(value = "{author}/albums", produces = "application/json")
    public ResponseEntity<?> listAuthorAlbums(
            @RequestParam String provider,
            @PathVariable String author
    ){
        return new ResponseEntity<>(service.getAuthorAlbums(author, provider), HttpStatus.OK);
    }

//    @GetMapping("{author}/albums/{albumId}")
//    public ResponseEntity<?> getAuthorAlbum(
//            @RequestParam String provider,
//            @PathVariable String author,
//            @PathVariable String albumId
//    ){
//        return new ResponseEntity<>(HttpStatus.ACCEPTED);
//    }

}
