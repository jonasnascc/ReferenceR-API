package be.wanna.Referencerback.controller;

import be.wanna.Referencerback.service.PhotoService;
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
            @RequestParam String url
    ){
        return new ResponseEntity<>(photoService.getDeviationInfoByUrl(url), HttpStatus.OK);
    }
}
