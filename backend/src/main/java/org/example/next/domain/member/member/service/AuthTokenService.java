package org.example.next.domain.member.member.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.example.next.domain.member.member.entity.Member;
import org.example.next.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    @Value(value = "${custom.jwt.secret-key}")
    private String keyString;

    String genAccessToken(Member member) {

        Claims claims =
                Jwts.claims()
                    .add("id", member.getId())
                    .add("username", member.getUsername())
                    .build();

        return Util.Jwt.createToken(keyString, claims);
    }

    Map<String, Object> getPayload(String token, String keyString) {

        if(!Util.Jwt.checkValid(token, keyString))
            return Map.of();

        Map<String, Object> claims = Util.Jwt.getClaims(token, keyString);


        Map<String, Object> payload = new HashMap<>(claims);
        payload.put("id", ((Number) claims.get("id")).longValue());

        return payload;
    }
}
