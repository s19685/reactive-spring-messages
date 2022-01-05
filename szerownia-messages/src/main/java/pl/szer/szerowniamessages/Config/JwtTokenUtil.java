package pl.szer.szerowniamessages.Config;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

@Log4j2
@Component
public class JwtTokenUtil implements Serializable {

    @Value("${jwt.secret}")
    private String sec;

    private static String secret;

    @Value("${jwt.secret}")
    public void setSecretStatic(String sec) {
        JwtTokenUtil.secret = sec;
    }

    public static String loggedId(String token) {
        if (token == null) throw new BadTokenException();

        try {
            var decodedToken = decodeToken(token.substring(6));
            return decodedToken.getBody().get("id").toString();

        } catch (ExpiredJwtException e) {
            log.error("Token has Expired");
            throw new ExpiredTokenException();
        }
    }

    private static Jws<Claims> decodeToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token);
    }

    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "cant process your request")
    private static class BadTokenException extends RuntimeException {
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "token expired")
    private static class ExpiredTokenException extends RuntimeException {
    }
}
