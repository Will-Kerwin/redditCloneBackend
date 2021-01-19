package co.uk.itskerwin.redditClone.controller;

import co.uk.itskerwin.redditClone.dto.AuthenticationResponse;
import co.uk.itskerwin.redditClone.dto.LoginRequest;
import co.uk.itskerwin.redditClone.dto.RegisterRequest;
import co.uk.itskerwin.redditClone.service.AuthService;
import lombok.AllArgsConstructor;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// controls what routes head where and then uses functions from services
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest){
        authService.signup(registerRequest);
        return new ResponseEntity<>("User Registration Successful",
                OK);
    }

    @GetMapping("/accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token){
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account activated Successfully", OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }
}
