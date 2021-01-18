package co.uk.itskerwin.redditClone.service;


import co.uk.itskerwin.redditClone.dto.RegisterRequest;
import co.uk.itskerwin.redditClone.model.NotificationEmail;
import co.uk.itskerwin.redditClone.model.User;
import co.uk.itskerwin.redditClone.model.VerificationToken;
import co.uk.itskerwin.redditClone.repository.UserRepository;
import co.uk.itskerwin.redditClone.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

// Has main business logic for requests
@Service
// All args does constructor injection
@AllArgsConstructor
public class AuthService {

    // Auto wired not recommended use constructor injection instead
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;

    // annotation used when interacting with database
    @Transactional
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
                "please click on teh below url to activate your account: " +
                "http://localhost:8080/api/auth/accountVerification/" +
                token));
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
}
