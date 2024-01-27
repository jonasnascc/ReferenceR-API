package be.wanna.Referencerback.controller;

import be.wanna.Referencerback.dto.ConnectionDTO;
import be.wanna.Referencerback.service.AlbumsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/author")
@AllArgsConstructor
public class PhotoController {
    private final AlbumsService service;
    @GetMapping(value = "{author}/albums/{albumId}/photos" )
    public ResponseEntity<?> getAlbumPhotos(
            @RequestParam String provider,
            @RequestParam Integer page,
            @RequestParam Integer limit,
            @RequestParam Integer maxThumbSize,
            @PathVariable String author,
            @PathVariable String albumId
            ){
        return new ResponseEntity<>(service.listPhotos(author, albumId, page, limit, provider, maxThumbSize), HttpStatus.OK);
    }



}
