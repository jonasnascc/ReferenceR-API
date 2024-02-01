package be.wanna.Referencerback.controller;

import be.wanna.Referencerback.service.photo.PhotoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deviations")
@AllArgsConstructor
public class DeviationController {
    private final PhotoService photoService;

    @GetMapping(produces = "application/json")
    public ResponseEntity<?> getDeviationInfoByUrl(
            @RequestHeader("Resources-provider") String provider,
            @RequestParam String url
    ){
        return new ResponseEntity<>(photoService.getDeviationInfoByUrl(url), HttpStatus.OK);
    }

    @GetMapping(value = "tags", produces = "application/json")
    public ResponseEntity<?> getTagsByUrl(
            @RequestHeader("Resources-provider") String provider,
            @RequestParam String url
    ){
        return new ResponseEntity<>(photoService.getTagsByUrl(url), HttpStatus.OK);
    }


}
