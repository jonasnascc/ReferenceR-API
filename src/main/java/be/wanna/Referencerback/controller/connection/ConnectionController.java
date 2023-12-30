package be.wanna.Referencerback.controller.connection;

import be.wanna.Referencerback.dto.ConnectionDTO;
import be.wanna.Referencerback.service.connection.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/connection")
@RequiredArgsConstructor
public class ConnectionController {
    private final ConnectionService connectionService;

    @PostMapping("/new")
    public ResponseEntity<?> newConnection(
            @RequestParam String provider,
            @RequestBody ConnectionDTO dto
    ) throws Exception {
        return new ResponseEntity<>(connectionService.newConnection(dto.username(), dto.password(), provider), HttpStatus.OK);
    }
}
