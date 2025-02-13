package org.example.next.post.comment;

import org.example.next.domain.member.member.entity.Member;
import org.example.next.domain.member.member.service.MemberService;
import org.example.next.domain.post.comment.controller.ApiV1CommentController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ApiV1CommentControllerTest {

    @Autowired
    private MockMvc mockmvc;
    @Autowired
    private MemberService memberService;

    private String token;
    private Member loginMember;


    @BeforeEach
    void setUp() {
        loginMember = memberService.findByUsername("user2").get();
        token = memberService.getAuthToken(loginMember);
    }

    @Test
    @DisplayName("댓글 작성")
    void write() throws Exception {
        Long postId = 1L;
        String content = "content";
        ResultActions perform = mockmvc.perform(
                post("/api/v1/posts/%d/comments".formatted(postId))
                        .content("""
                                {
                                    "content": "%s"
                                }
                                """
                                .stripIndent()
                                .formatted(content))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .header("Authorization", "Bearer " + token)
        );

        perform
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(ApiV1CommentController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(jsonPath("$.code").value("201-1"));
    }

    @Test
    @DisplayName("댓글 조회")
    void getItem() throws Exception {
        Long postId = 1L;
        ResultActions perform = mockmvc
                .perform(
                        get("/api/v1/posts/%d/comments".formatted(postId))
                );

        perform
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1CommentController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.length()").value(2));
    }
    @Test
    @DisplayName("댓글 수정")
    void modify() throws Exception {
        Long postId = 1L;
        Long commentId = 1L;
        String content = "content";

        Member user1 = memberService.findByUsername("user1").get();
        String authToken = memberService.getAuthToken(user1);

        ResultActions perform = mockmvc
                .perform(
                        put("/api/v1/posts/%d/comments/%d"
                                .formatted(postId, commentId))
                                .header("Authorization", "Bearer " +authToken )
                                .content("""
                                        {
                                            "content": "%s"
                                        }
                                        """
                                        .stripIndent()
                                        .formatted(content))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                );

        perform
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1CommentController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 댓글 수정이 완료되었습니다.".formatted(commentId)));
    }

    @Test
    @DisplayName("댓글 삭제")
    void delete1() throws Exception {
        Long postId = 1L;
        Long commentId = 1L;

        Member user1 = memberService.findByUsername("user1").get();
        String authToken = memberService.getAuthToken(user1);
        ResultActions perform = mockmvc
                .perform(
                        delete("/api/v1/posts/%d/comments/%d".formatted(postId, commentId))
                                .header("Authorization", "Bearer " +authToken)
                );
        perform
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1CommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value(String.format("%d번 댓글 삭제가 완료되었습니다.", commentId)));
    }
    @Test
    @DisplayName("댓글 다건 조회")
    void items() throws Exception {

        long postId = 1;

        ResultActions resultActions = mockmvc
                .perform(
                        get("/api/v1/posts/%d/comments".formatted(postId)
                        )
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1CommentController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }
}
