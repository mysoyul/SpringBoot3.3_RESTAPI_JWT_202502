package com.boot3.myrestapi.security.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtService {
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    public static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());
    private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;
    public static final int ACCESS_EXPIRE = 3600;

    //JWT 토큰에 저장된 정보 추출
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //JWT 토큰 검증
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    //JWT 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException(
                    "JWT was exprired or incorrect",
                    ex.fillInStackTrace());
        }
    }

    //JWT 토큰 생성
    public String generateToken(String userName){
        // ACCESS_EXPIRE 3600초 => 60분
        Date exprireDate = Date.from(Instant.now().plusSeconds(ACCESS_EXPIRE));

        return Jwts.builder() //JwtBuilder
                //시크릿키 + 알고리즘,
                .signWith(KEY, ALGORITHM)
                //payload 내부에 subject
                .subject(userName)
                //payload 내부에 토큰생성시간
                .issuedAt(new Date())
                //토큰만료시간
                .expiration(exprireDate)
                .compact();
    }

}