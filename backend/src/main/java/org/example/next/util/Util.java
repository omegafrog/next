package org.example.next.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.example.next.global.dto.RsData;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class Util {

    private static final ObjectMapper objectMapper = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    public static class Json{

        public static <T> String toJson(RsData<T> data)  {
            try {
                return objectMapper.writeValueAsString(data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static class Jwt{

        private final static int expireSeconds = 60 * 60 * 24 * 365;

        public static String createToken(String keyString, Map<String, Object> claims) {

            SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());
            Date issuedAt = new Date();
            Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);
            return Jwts.builder()
                    .claims(claims)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .signWith(secretKey)
                    .compact();
        }

        public static boolean checkValid(String jwtToken, String keyString){
            try{
                SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());
                Jwts
                        .parser()
                        .verifyWith(secretKey)
                        .build()
                        .parse(jwtToken);
                return true;
            }catch (RuntimeException e){
                log.error(e.getMessage(), e);
                return false;
            }
        }

        public static Map<String, Object> getClaims(String token, String keyString ){

            SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());

            if(token.isEmpty())
                return Map.of();

            return (Map<String, Object>) Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parse(token).getPayload();
        }
    }
}
