package be.wanna.Referencerback.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/author/")
public class AuthorController {
    @GetMapping("{author}")
    public ResponseEntity<?> getAuthor(
            @RequestParam String provider,
            @PathVariable String author
    ){
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
