package co.uk.itskerwin.redditClone.service;


import co.uk.itskerwin.redditClone.dto.AuthenticationResponse;
import co.uk.itskerwin.redditClone.dto.LoginRequest;
import co.uk.itskerwin.redditClone.dto.RegisterRequest;
import co.uk.itskerwin.redditClone.exception.SpringRedditException;
import co.uk.itskerwin.redditClone.model.NotificationEmail;
import co.uk.itskerwin.redditClone.model.User;
import co.uk.itskerwin.redditClone.model.VerificationToken;
import co.uk.itskerwin.redditClone.repository.UserRepository;
import co.uk.itskerwin.redditClone.repository.VerificationTokenRepository;
import co.uk.itskerwin.redditClone.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

// Has main business logic for requests
@Service
// All args does constructor injection
@AllArgsConstructor
@Transactional
public class AuthService {

    // Auto wired not recommended use constructor injection instead
    // this injects classes into AuthService for use
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    // annotation used when interacting with database
    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setEnabled(false);

        // saves to database via repository
        userRepository.save(user);

        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationEmail("Please Activate your Account", user.getEmail(),
                "Thank you for signing up to Spring Reddit, " +
                        "please click on the below url to activate your account: " +
                        "http://localhost:8080/api/auth/accountVerification/" +
                        token));
    }

    public User getCurrentUser(){
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(()-> new UsernameNotFoundException("User name not found "+ principal.getUsername()));
    }

    private String generateVerificationToken(User user) {
        // generates new random uuid
        String token = UUID.randomUUID().toString();

        // instantiates the class
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        // saves
        verificationTokenRepository.save(verificationToken);

        // returns token for later use
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token"));
        fetchUserAndEnable(verificationToken.get());
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new SpringRedditException("User not found with name: " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        //Uses spring to do most of the auth
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        //sets the auth context with what we want i.e. username/password
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        // returns a new data transfer object
        return new AuthenticationResponse(token, loginRequest.getUsername());


    }
}
