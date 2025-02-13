package org.example.next.domain.post.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.next.domain.member.member.entity.Member;
import org.example.next.domain.post.post.dto.PageDto;
import org.example.next.domain.post.post.dto.PostWithContentDto;
import org.example.next.domain.post.post.entity.Post;
import org.example.next.domain.post.post.service.PostService;
import org.example.next.global.Rq;
import org.example.next.global.dto.EmptyData;
import org.example.next.global.dto.RsData;
import org.example.next.global.exception.ServiceException;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ApiV1PostController", description = "글 API")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class ApiV1PostController {

    private final PostService postService;
    private final Rq rq;

    @Operation(
            summary = "글 목록 조회",
            description = "페이징 처리와 검색 가능"
    )
    @GetMapping
    public RsData<PageDto> getItems(@RequestParam(name = "page", defaultValue = "0") int currentPageNum,
                                    @RequestParam(name = "size", defaultValue = "5") int pageSize,
                                    @RequestParam(name = "keyword", required = false) String keyword,
                                    @RequestParam(name="keyword-type", required = false, defaultValue = "title") KeywordType keywordType) {

        Page<Post> posts;
        if (keyword != null && keywordType.equals(KeywordType.title))
            posts = postService.searchItemByTitle(currentPageNum, pageSize, keyword);
        else if (keyword != null && keywordType.equals(KeywordType.content))
            posts = postService.searchItemByContent(currentPageNum, pageSize, keyword);
        else
            posts = postService.getItems(currentPageNum, pageSize);

        return new RsData<>(
                "200-1",
                "글 목록 조회가 완료되었습니다.",
                new PageDto(posts)
        );
    }

    @Operation(
            summary = "내 글 목록 조회",
            description = "페이징 처리와 검색 가능"
    )
    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/me")
    public RsData<PageDto> getMines(@RequestParam(name = "page", defaultValue = "0") int currentPageNum,
                                      @RequestParam(name = "size", defaultValue = "5") int pageSize,
                                      @RequestParam(name = "keyword", required = false) String keyword,
                                      @RequestParam(name="keyword-type", required = false, defaultValue = "title") KeywordType keywordType) {

        Member member = rq.getActor();

        Page<Post> posts;
        if(keyword != null && keywordType.equals(KeywordType.title))
            posts = postService.getMinesByTitle(member, currentPageNum, pageSize, keyword);
        else if (keyword != null && keywordType.equals(KeywordType.content))
            posts = postService.getMinesByContent(member, currentPageNum, pageSize, keyword);
        else
            posts = postService.getMines(member, currentPageNum, pageSize);

        return new RsData<>(
                "200-1",
                "내 글 조회 성공.",
                new PageDto(posts)
        );
    }

    @Operation(
            summary = "글 단건 조회",
            description = "비밀글은 작성자만 조회 가능"
    )
    @GetMapping("{id}")
    public RsData<PostWithContentDto> getItem(@PathVariable long id) {

        Post post = postService.getItem(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 글입니다."));

        if (!post.isOpened()) {
            Member member = rq.getActor();
            post.canRead(member);
        }

        return new RsData<>(
                "200-1",
                "글 조회가 완료되었습니다.",
                new PostWithContentDto(post)
        );
    }

    @Operation(
            summary = "글 삭제",
            description = "작성자와 관리자만 글 삭제 가능"
    )
    @DeleteMapping("/{id}")
    public RsData<EmptyData> delete(@PathVariable long id) {

        Member actor = rq.getActor();
        Post post = postService.getItem(id).get();

        post.canDelete(actor);
        postService.delete(post);

        return new RsData<>(
                "200-1",
                "%d번 글 삭제가 완료되었습니다.".formatted(id)
        );
    }


    record ModifyReqBody(@NotBlank @Length(min = 3) String title,
                         @NotBlank @Length(min = 3) String content,
                         boolean opened,
                         boolean listed) {
    }

    @Operation(
            summary = "글 수정",
            description = "작성자와 관리자만 글 수정 가능"
    )
    @PutMapping("{id}")
    public RsData<PostWithContentDto> modify(@PathVariable long id, @RequestBody @Valid ModifyReqBody body
    ) {

        Member actor = rq.getActor();
        Post post = postService.getItem(id).get();

        if (post.getAuthor().getUsername().equals("admin") || post.getAuthor().getId() != actor.getId()) {
            throw new ServiceException("403-1", "자신이 작성한 글만 수정 가능합니다.");
        }

        post.canModify(actor);
        Post modify = postService.modify(post, body.title(), body.content(), body.opened, body.listed);
        return new RsData<>(
                "200-1",
                "%d번 글 수정이 완료되었습니다.".formatted(id),
                new PostWithContentDto(modify)
        );
    }

    record WriteReqBody(
            @NotBlank @Length(min = 3) String title,
            @NotBlank @Length(min = 3) String content,
            boolean opened,
            boolean listed
    ) {
    }

    @Operation(
            summary = "글 작성",
            description = "로그인 한 사용자만 글 작성 가능"
    )
    @PostMapping
    public RsData<PostWithContentDto> write(@RequestBody @Valid WriteReqBody body,
                                            @AuthenticationPrincipal UserDetails principal) {

        Member actor = rq.getActor();

        Post post = postService.write(actor, body.title(), body.content(), body.opened, body.listed);

        return new RsData<>(
                "200-1",
                "글 작성이 완료되었습니다.",
                new PostWithContentDto(post)
        );
    }

    record StatisticsResBody(long postCount, long postPublishedCount, long postListedCount) {
    }

    @Operation(
            summary = "통계 조회"
    )
    @GetMapping("/statistics")
    public RsData<StatisticsResBody> getStatistics() {
        Member actor = rq.getActor();
        if(!actor.isAdmin()) {
            throw new ServiceException("403-1", "접근 권한이 없습니다.");
        }
        return new RsData<>(
                "200-1",
                "통계 조회가 완료되었습니다.",
                new StatisticsResBody(
                        10,
                        10,
                        10
                )
        );
    }
}
