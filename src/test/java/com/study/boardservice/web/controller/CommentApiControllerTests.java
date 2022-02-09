package com.study.boardservice.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.boardservice.domain.comment.Comment;
import com.study.boardservice.domain.comment.CommentRepository;
import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.member.MemberRepository;
import com.study.boardservice.domain.post.Post;
import com.study.boardservice.domain.post.PostRepository;
import com.study.boardservice.web.dto.CommentDto;
import com.study.boardservice.web.dto.SessionMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.study.boardservice.config.TestValue.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class CommentApiControllerTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Member author;
    private Post post;
    private String commentContent = "test comment";

    @BeforeEach
    public void setUp(){
        author = memberRepository.save(Member.builder().email(EMAIL).password(PASSWORD).name(NAME).phone(PHONE).build());
        post = postRepository.save(Post.builder().title(TITLE).content(CONTENT).author(author).build());
    }

    @Test
    public void 댓글_생성하기() throws Exception {
        // given
        CommentDto request = CommentDto.builder().content(commentContent).build();
        String url = "/api/v1/posts/" + post.getId() + "/comments";

        // when
        ResultActions actions = mvc.perform(post(url).sessionAttr("member", new SessionMember(EMAIL, NAME))
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isCreated());
        Comment comment = commentRepository.findAll().get(0);
        assertThat(comment.getContent()).isEqualTo(commentContent);
        assertThat(comment.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(comment.getPost().getId()).isEqualTo(post.getId());
        assertThat(comment.getCreatedAt()).isBefore(LocalDateTime.now());
        assertThat(comment.getModifiedAt()).isBefore(LocalDateTime.now());
    }

    @Test
    public void 필수값_누락으로_인한_댓글_생성하기_실패() throws Exception {
        // given
        CommentDto request = CommentDto.builder().build();
        String url = "/api/v1/posts/" + post.getId() + "/comments";

        // when
        ResultActions actions = mvc.perform(post(url).sessionAttr("member", new SessionMember(EMAIL, NAME))
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 비로그인에_의한_댓글_생성하기_실패() throws Exception {
        // given
        CommentDto request = CommentDto.builder().content(commentContent).build();
        String url = "/api/v1/posts/" + post.getId() + "/comments";

        // when
        ResultActions actions = mvc.perform(post(url)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isUnauthorized());
    }

    @Test
    public void 존재하지_않는_계정에_의한_댓글_생성하기_실패() throws Exception {
        // given
        CommentDto request = CommentDto.builder().content(commentContent).build();
        String url = "/api/v1/posts/" + post.getId() + "/comments";

        // when
        ResultActions actions = mvc.perform(post(url).sessionAttr("member", new SessionMember("invalid", "invalid"))
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 존재하지_않는_게시글에_의한_댓글_생성하기_실패() throws Exception {
        // given
        CommentDto request = CommentDto.builder().content(commentContent).build();
        String url = "/api/v1/posts/-1/comments";

        // when
        ResultActions actions = mvc.perform(post(url).sessionAttr("member", new SessionMember(EMAIL, NAME))
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    public void 댓글_생성으로_인한_게시글_엔티티의_댓글_증가() throws Exception {
        // given
        CommentDto request = CommentDto.builder().content(commentContent).build();
        String url = "/api/v1/posts/" + post.getId() + "/comments";

        // when
        mvc.perform(post(url).sessionAttr("member", new SessionMember(EMAIL, NAME))
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        Post commentSavedPost = postRepository.findAll().get(0);
        assertThat(commentSavedPost.getComments().size()).isGreaterThan(0);
    }

    @Test
    public void 게시글_id로_댓글_조회하기() throws Exception {
        // given
        commentRepository.save(Comment.builder().content(commentContent).author(author).post(post).build());
        String url = "/api/v1/posts/" + post.getId() + "/comments";

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(commentContent))
                .andExpect(jsonPath("$[0].authorName").value(author.getName()))
                .andExpect(jsonPath("$[0].postId").value(post.getId()))
                .andExpect(jsonPath("$[0].createdAt").exists())
                .andExpect(jsonPath("$[0].modifiedAt").exists());
    }

    @Test
    public void 존재하지_않는_게시글로_인한_댓글_조회하기_실패() throws Exception {
        // given
        commentRepository.save(Comment.builder().content(commentContent).author(author).post(post).build());
        String url = "/api/v1/posts/-1/comments";

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 댓글_수정하기() throws Exception {
        // given
        String modifiedContent = "modified content";
        Long commentId = commentRepository.save(Comment.builder().content(commentContent).author(author).post(post).build()).getId();
        CommentDto request = CommentDto.builder().content(modifiedContent).build();
        String url = "/api/v1/posts/" + post.getId() +"/comments/" + commentId;

        // when
        ResultActions actions = mvc.perform(put(url).sessionAttr("member", new SessionMember(EMAIL, NAME))
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isOk());
        Comment comment = commentRepository.findAll().get(0);
        assertThat(comment.getContent()).isEqualTo(modifiedContent);
    }
}
