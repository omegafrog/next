package org.example.next.post.post;

import jakarta.transaction.Transactional;
import org.example.next.domain.member.member.entity.Member;
import org.example.next.domain.member.member.service.MemberService;
import org.example.next.domain.post.post.controller.ApiV1PostController;
import org.example.next.domain.post.post.entity.Post;
import org.example.next.domain.post.post.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ApiV1PostControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private PostService postService;
    @Autowired
    private MemberService memberService;

    private String token1;
    private String token2;
    private Member loginMember;
    private Member loginMember2;


    @BeforeEach
    void setUp() {
        loginMember = memberService.findByUsername("user2").get();
        loginMember2 = memberService.findByUsername("user1").get();;
        token1 = memberService.getAuthToken(loginMember)+"/"+loginMember.getApiKey();
        token2 = memberService.getAuthToken(loginMember2)+"/"+loginMember2.getApiKey();
    }

    @Test
    @DisplayName("다건 조회")
    @Transactional
    void getPosts() throws Exception {

        int pageSize = 5;
        int pageNum = 0;

        ResultActions resultActions= mvc.perform(
                get("/api/v1/posts?page=%d&size=%d".formatted(pageNum, pageSize))
        );

        Page<Post> items = postService.getItems(pageNum, pageSize);

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("글 목록 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data.items.length()").value( pageSize)) // 한페이지당 보여줄 글 개수
                .andExpect(jsonPath("$.data.currentPageNum").isNumber()) // 현재 페이지
                .andExpect(jsonPath("$.data.totalPageNum").isNumber()); // 전체 페이지 개수


        for(int i = 0; i < items.getContent().size(); i++) {

            Post post = items.getContent().get(i);

            resultActions
                    .andExpect(jsonPath("$.data.items[%d]".formatted(i)).exists())
                    .andExpect(jsonPath("$.data.items[%d].id".formatted(i)).value(post.getId()))
                    .andExpect(jsonPath("$.data.items[%d].title".formatted(i)).value(post.getTitle()))
                    .andExpect(jsonPath("$.data.items[%d].content".formatted(i)).doesNotExist())
                    .andExpect(jsonPath("$.data.items[%d].authorId".formatted(i)).value(post.getAuthor().getId()))
                    .andExpect(jsonPath("$.data.items[%d].authorName".formatted(i)).value(post.getAuthor().getNickname()))
                    .andExpect(jsonPath("$.data.items[%d].opened".formatted(i)).value(post.isOpened()))
                    .andExpect(jsonPath("$.data.items[%d].listed".formatted(i)).value(post.isListed()))
                    .andExpect(jsonPath("$.data.items[%d].createdDatetime".formatted(i)).value(matchesPattern(post.getCreatedDate().toString().replaceAll("0+$", "") + ".*")))
                    .andExpect(jsonPath("$.data.items[%d].modifiedDatetime".formatted(i)).value(matchesPattern(post.getModifiedDate().toString().replaceAll("0+$", "") + ".*")));
        }
    }
    @Test
    @DisplayName("다건 조회 - 검색")
    void search() throws Exception {

        int pageNum=0;
        int pageSize=5;
        String keyword = "titl";

        ResultActions resultActions= mvc.perform(
                get("/api/v1/posts?page=%d&size=%d&keyword=%s".formatted(pageNum, pageSize, keyword))
        );

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.data.items.length()").value(5))
                .andExpect(jsonPath("$.data.currentPageNum").isNumber())
                .andExpect(jsonPath("$.data.totalPageNum").isNumber())
                .andExpect(jsonPath("$.data.totalElementSize").value(157));
    }

    @Test
    @DisplayName("다건 조회 - content 검색")
    void search2() throws Exception {

        int pageNum=0;
        int pageSize=5;
        String keyword = "content";

        ResultActions resultActions= mvc.perform(
                get("/api/v1/posts?page=%d&size=%d&keyword-type=content&keyword=%s".formatted(pageNum, pageSize, keyword))
        );

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.data.items.length()").value(5))
                .andExpect(jsonPath("$.data.currentPageNum").isNumber())
                .andExpect(jsonPath("$.data.totalPageNum").isNumber())
                .andExpect(jsonPath("$.data.totalElementSize").value(157));
    }


    @Test
    @DisplayName("내 글 조회 - 페이징, 검색 적용")
    @Transactional
    void getPost4() throws Exception {
        String keywordType = "title";
        String keyword = "title3";
        int page = 0;
        int size = 5;
        ResultActions perform = mvc.perform(get("/api/v1/posts/me?keyword-type=%s&keyword=%s&page=%d&size=%d"
                .formatted(keywordType, keyword, page, size))
                .header("Authorization", "Bearer " + token1));

        perform
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getMines"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.data.items.length()").value(3))
                .andExpect(jsonPath("$.data.currentPageNum").isNumber())
                .andExpect(jsonPath("$.data.totalPageNum").value(1))
                .andExpect(jsonPath("$.data.totalElementSize").value(3))
                .andDo(print());

        Member member = memberService.findByUsername("user2").get();
        Page<Post> title3 = postService.getMinesByTitle(member, page, size, "title3");
        for (int i = 0; i < title3.getContent().size(); i++) {
            Post post = title3.getContent().get(i);
            perform
                    .andExpect(jsonPath("$.data.items[%d]".formatted(i)).exists())
                    .andExpect(jsonPath("$.data.items[%d].id".formatted(i)).value(post.getId()))
                    .andExpect(jsonPath("$.data.items[%d].title".formatted(i)).value(post.getTitle()))
                    .andExpect(jsonPath("$.data.items[%d].content".formatted(i)).doesNotExist())
                    .andExpect(jsonPath("$.data.items[%d].authorId".formatted(i)).value(post.getAuthor().getId()))
                    .andExpect(jsonPath("$.data.items[%d].authorName".formatted(i)).value(post.getAuthor().getNickname()))
                    .andExpect(jsonPath("$.data.items[%d].opened".formatted(i)).value(post.isOpened()))
                    .andExpect(jsonPath("$.data.items[%d].listed".formatted(i)).value(post.isListed()))
                    .andExpect(jsonPath("$.data.items[%d].createdDatetime".formatted(i)).value(matchesPattern(post.getCreatedDate().toString().replaceAll("0+$", "") + ".*")))
                    .andExpect(jsonPath("$.data.items[%d].modifiedDatetime".formatted(i)).value(matchesPattern(post.getModifiedDate().toString().replaceAll("0+$", "") + ".*")));
        }
    }

    @Test
    @DisplayName("단건 조회")
    void getPost() throws Exception {
        Long id = 1L;

        ResultActions resultActions = mvc.perform(
                get("/api/v1/posts/%d".formatted(id))
        );

        Post post = postService.getItem(id).get();

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("글 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.title").value(post.getTitle()))
                .andExpect(jsonPath("$.data.content").value(post.getContent()))
                .andExpect(jsonPath("$.data.authorId").value(post.getAuthor().getId()))
                .andExpect(jsonPath("$.data.createdDatetime").value(
                        matchesPattern(post.getCreatedDate().toString().replaceAll("0+$", "") + ".*")))
                .andExpect(jsonPath("$.data.modifiedDatetime").value(
                        matchesPattern(post.getModifiedDate().toString().replaceAll("0+$", "") + ".*")))
                .andExpect(jsonPath("$.data.opened").value("true"))
                .andExpect(jsonPath("$.data.listed").value("true"));

    }

    @Test
    @DisplayName("단건 조회 - 없는 글 조회")
    void getPost2() throws Exception {
        Long id = 9999L;

        ResultActions resultActions = mvc.perform(
                get("/api/v1/posts/%d".formatted(id))
        );

        resultActions
                .andExpect(status().isNotFound())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(jsonPath("$.code").value("404-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 글입니다."));
    }

    @Test
    @DisplayName("단건 조회 - 비밀 글 작성자 아닌 사람이 조회")
    void getPost3() throws Exception {
        Long id = 3L;

        ResultActions resultActions = mvc.perform(
                get("/api/v1/posts/%d".formatted(id))
                        .header("Authorization", "Bearer " + token2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );

        resultActions
                .andExpect(status().isForbidden())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(jsonPath("$.code").value("403-1"))
                .andExpect(jsonPath("$.msg").value("비공개된 글입니다."));
    }

    private ResultActions writePost(String title, String content, String apiKey) throws Exception {
        return mvc.perform(
                post("/api/v1/posts")
                        .content("""
                                {
                                    "title": "%s",
                                    "content": "%s"
                                }
                                """.formatted(title, content)
                                .stripIndent()
                        )
                        .header("Authorization", "Bearer %s".formatted(apiKey))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );
    }

    @Test
    @DisplayName("글 작성")
    void writePost() throws Exception {
        String title = "title";
        String content = "content";

        ResultActions perform = writePost(title, content, token1);
        perform
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("글 작성이 완료되었습니다."))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.content").value(content));
    }

    @Test
    @DisplayName("로그인 없이 글 작성")
    void writePostWithoutApiKey() throws Exception {
        String title = "title";
        String content = "content";

        ResultActions perform = writePost(title, content, "");
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401-1"))
                .andExpect(jsonPath("$.msg").value("인증이 필요합니다."));
    }

    @Test
    @DisplayName("body 없음")
    void writePostWithoutBody() throws Exception {
        String title = "";
        String content = "content";

        ResultActions perform = writePost(title, content, token1);
        perform
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.msg").value("title : Length : length must be between 3 and 2147483647\ntitle : NotBlank : must not be blank"));
    }

    private ResultActions modifyPost(Long postId, String title, String content, boolean opened, boolean listed, String apiKey) throws Exception {
        return mvc.perform(
                put("/api/v1/posts/%d".formatted(postId))
                        .content("""
                                {
                                    "title": "%s",
                                    "content": "%s",
                                    "opened": "%s",
                                    "listed": "%s"
                                }
                                """.formatted(title, content, opened, listed)
                                .stripIndent()
                        )
                        .header("Authorization", "Bearer %s".formatted(apiKey))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );
    }

    @Test
    @DisplayName("글 수정")
    void updatePost() throws Exception {
        Long postId = 1L;
        String title = "changedTitle";
        String content = "changedContent";
        ResultActions perform = modifyPost(postId, title, content, true, true, token2);
        perform
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modifyPost"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글 수정이 완료되었습니다.".formatted(postId)))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.title").value(title))
                .andExpect(jsonPath("$.data.content").value(content));
    }

    @Test
    @DisplayName("글 수정 - no apiKey")
    void updatePost2() throws Exception {
        Long postId = 1L;
        String title = "changedTitle";
        String content = "changedContent";
        ResultActions perform = modifyPost(postId, title, content, true, true, "");
        perform
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401-1"))
                .andExpect(jsonPath("$.msg").value("인증이 필요합니다."));
    }

    @Test
    @DisplayName("글 수정 - not owner ")
    void updatePost3() throws Exception {
        Long postId = 1L;
        String title = "changedTitle";
        String content = "changedContent";
        Member user3 = memberService.findByUsername("user3").get();
        String authToken = memberService.getAuthToken(user3)+"/"+user3.getApiKey();
        ResultActions perform = modifyPost(postId, title, content, true, true,authToken );
        perform
                .andExpect(status().isForbidden())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modifyPost"))
                .andExpect(jsonPath("$.code").value("403-1"))
                .andExpect(jsonPath("$.msg").value("자신이 작성한 글만 수정 가능합니다."));
    }

    @Test
    @DisplayName("글 수정 - no input data ")
    void updatePost4() throws Exception {
        Long postId = 1L;
        String title = "changedTitle";
        String content = "";
        ResultActions perform = modifyPost(postId, title, content, true, true, token1);
        perform
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modifyPost"))
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.msg").value("content : Length : length must be between 3 and 2147483647\ncontent : NotBlank : must not be blank"));
    }

    @Test
    @DisplayName("글 수정 - 관리자가 수정")
    void updatePost5() throws Exception {
        Long postId = 1L;
        String title = "changedTitle";
        String content = "";
        Member admin = memberService.findByUsername("admin").get();
        String authToken = memberService.getAuthToken(admin)+"/"+admin.getApiKey();
        ResultActions perform = modifyPost(postId, title, content, true, true, authToken);
        perform
                .andExpect(status().isBadRequest())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modifyPost"))
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.msg").value("content : Length : length must be between 3 and 2147483647\ncontent : NotBlank : must not be blank"));
    }

    private ResultActions deletePost(Long postId, String apiKey) throws Exception {
        return mvc
                .perform(
                        delete("/api/v1/posts/%d".formatted(postId))
                                .header("Authorization", "Bearer %s".formatted(apiKey))
                );
    }

    @Test
    @DisplayName("글 삭제")
    void deletePost() throws Exception {
        Long postId = 1L;
        ResultActions resultActions = deletePost(postId, token2);
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글 삭제가 완료되었습니다.".formatted(postId)));
    }

    @Test
    @DisplayName("글 삭제 - no apiKey")
    void deletePost2() throws Exception {
        Long postId = 1L;
        String apiKey = "";
        ResultActions resultActions = deletePost(postId, "");
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401-1"))
                .andExpect(jsonPath("$.msg").value("인증이 필요합니다."));
    }

    @Test
    @DisplayName("글 삭제 - not owner")
    void deletePost3() throws Exception {
        Long postId = 1L;
        ResultActions resultActions = deletePost(postId, token1);
        resultActions
                .andExpect(status().isForbidden())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(jsonPath("$.code").value("403-1"))
                .andExpect(jsonPath("$.msg").value("자신이 작성한 글만 삭제 가능합니다."));
    }
    @Test
    @DisplayName("통계")
    @WithUserDetails("admin")
    void statistics() throws Exception {
        ResultActions resultActions = mvc.perform(
                        get("/api/v1/posts/statistics")
                )
                .andDo(print());
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("getStatistics"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("통계 조회가 완료되었습니다."))
                .andExpect(jsonPath("$.data.postCount").value(10))
                .andExpect(jsonPath("$.data.postPublishedCount").value(10))
                .andExpect(jsonPath("$.data.postListedCount").value(10));
    }
    @Test
    @DisplayName("통계 - 관리자 기능 - user1 접근")
    @WithUserDetails("user1")
    void statisticsUser() throws Exception {
        ResultActions resultActions = mvc.perform(
                        get("/api/v1/posts/statistics")
                )
                .andDo(print());
        resultActions
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403-1"))
                .andExpect(jsonPath("$.msg").value("접근 권한이 없습니다."));
    }
}
