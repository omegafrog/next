package org.example.next.domain.member.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.next.domain.member.member.dto.MemberDto;
import org.example.next.domain.member.member.entity.Member;
import org.example.next.domain.member.member.service.MemberService;
import org.example.next.global.Rq;
import org.example.next.global.dto.RsData;
import org.example.next.global.exception.ServiceException;
import org.springframework.web.bind.annotation.*;


@Tag(name = "ApiV1MemberController", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiV1MemberController {

    private final MemberService memberService;
    private final Rq rq;

    record JoinReqBody(String username, String password, String nickname) {}

    @Operation(summary = "회원 가입")
    @PostMapping(value = "/join", produces = "application/json;charset=UTF-8")
    public RsData<MemberDto> join(@RequestBody JoinReqBody reqBody) {

        memberService.findByUsername(reqBody.username())
                .ifPresent(_ -> {
                    throw new ServiceException("409-1", "이미 사용중인 아이디입니다.");
                });


        Member member = memberService.join(reqBody.username(), reqBody.password(), reqBody.nickname());
        return new RsData<>(
                "201-1",
                "회원 가입이 완료되었습니다.",
                new MemberDto(member)
                );
    }

    public record LoginResBody(MemberDto item, String apiKey, String authToken){}

    public record LoginReqBody(@NotBlank String username, @NotBlank String password){}

    @Operation(summary = "로그인", description = "로그인 성공 시 ApiKey와 AccessToken 반환. 쿠키로도 반환")
    @PostMapping("/login")
    public RsData<LoginResBody> login(@Valid @RequestBody LoginReqBody reqBody, HttpServletResponse response) {
        Member member = memberService.login(reqBody.username, reqBody.password);

        String authToken = memberService.getAuthToken(member);

        rq.addCookie("accessToken", authToken);
        rq.addCookie("apiKey", member.getApiKey());

        return new RsData<>("200-1", "%s님 환영합니다.".formatted(member.getNickname()),
                new LoginResBody(new MemberDto(member), member.getApiKey(), authToken));
    }

    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public RsData<MemberDto> me() {

        Member actor = rq.getActor();
        Member member = memberService.findByUsername(actor.getUsername())
                .orElseThrow(() -> new ServiceException("401-1", "잘못된 인증 정보입니다."));

        return new RsData<>("200-1", "유저 정보 조회 성공", new MemberDto(member));
    }

    @Operation(summary = "로그아웃", description = "로그아웃 시 쿠키 삭제")
    @DeleteMapping("/logout")
    public RsData<Void> logout() {
        rq.removeCookie("accessToken");
        rq.removeCookie("apiKey");
        return new RsData<>("200-1", "로그아웃 되었습니다.");
    }
}
