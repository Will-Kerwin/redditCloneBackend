package co.uk.itskerwin.redditClone.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.*;
import java.time.Instant;
import java.util.Date;

@Service
@Slf4j
public class JwtProvider {

    //private KeyStore keyStore;
    private Key key;
    @Value("${jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    @PostConstruct
    public void init() {
        key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        /* look to private key
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/springBlog.jks");
            keyStore.load(resourceAsStream,"spring".toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e){
            throw new SpringRedditException("Exception occurred while loading keystore");
        }*/
    }

    public String generateToken(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        return Jwts
                .builder()
                .setSubject(principal.getUsername())
                //.signWith(getPrivateKey())
                .signWith(key)
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    public String generateTokenWithUsername(String username) {
        return Jwts
                .builder()
                .setSubject(username)
                //.signWith(getPrivateKey())
                .signWith(key)
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    // was valid way to do this however don't know how to get the jks but code works
    /*
    private PrivateKey getPrivateKey(){
        try {
            PrivateKey springBlog = (PrivateKey) keyStore.getKey("springBlog", "spring".toCharArray());
            log.error(springBlog.toString());
            return springBlog;
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e){
            throw new SpringRedditException("Exception occurred while retrieving public key from keystore");
        }
    }*/

    public boolean validateToken(String jwt) {
        Jwts.parser().setSigningKey(key).parseClaimsJws(jwt);
        return true;
    }

    // used for getting public key but need to work out jks first
    /*private PublicKey getPublicKey(){
        try{
            return keyStore.getCertificate("springBlog").getPublicKey();
        } catch (KeyStoreException e) {
            throw new SpringRedditException("Exception occurred while retrieving public key");
        }
    }*/

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public Long getJwtExpirationInMillis(){
        return jwtExpirationInMillis;
    }
}
