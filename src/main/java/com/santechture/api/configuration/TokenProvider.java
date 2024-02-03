package com.santechture.api.configuration;

import com.santechture.api.exception.BusinessExceptions;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    private static final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";
    private static final String SECRET_KEY = "QnhHZ3AxZk0xaWFTfCN2XndaI1JJKmp6aU8xWElAb1RYSVVtWl9LM28tcis/LSpLcHo1Sm8xQHQ5eCYmaFI0dlErbFpkaktQZTQwYVV4USRWIyZBREVtQCZZJTJuI3w0bEQ4ZmZGVU50XlF4MzNsKzdCbz1hM1JoOUxXRUlfTGk=";
    private static final String TOKEN_VALIDITY_MILLI = "3600000";

    private Key key;
    private long tokenValidityInMilliseconds;


    @PostConstruct
    public void init() throws Exception {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds = Long.parseLong((TOKEN_VALIDITY_MILLI));
    }

    public String createToken(Authentication authentication) {
        // todo we need to create table for Roles
        //  and retreive roles from db related to current principle
        String authorities = "admin";
        long now = (new Date()).getTime();

        Date validity  = new Date(now + this.tokenValidityInMilliseconds);
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    @SneakyThrows
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token.");
            log.error("Invalid JWT token trace.", e);
            throw new BusinessExceptions("invalid.token", HttpStatus.BAD_REQUEST);

        }

    }
}
