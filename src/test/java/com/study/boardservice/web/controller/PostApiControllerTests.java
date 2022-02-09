package com.study.boardservice.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.member.MemberRepository;
import com.study.boardservice.domain.post.Post;
import com.study.boardservice.domain.post.PostRepository;
import com.study.boardservice.web.dto.PostDto;
import com.study.boardservice.web.dto.SessionMember;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class PostApiControllerTests {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MockMvc mvc;

    private String email = "abc12345@abcde.com";
    private String password = "abc12345";
    private String name = "Teddy";
    private String phone = "00000000000";

    private String title = "Hello!";
    private String content = "Hello World!";
    private Member author;

    @BeforeEach
    public void setUp(){
        author = memberRepository.save(Member.builder().email(email).password(password).name(name).phone(phone).build());
    }

    @AfterEach
    public void tearDown(){
        postRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    public void 모든_게시글_조회하기() throws Exception {
        // given
        postRepository.save(Post.builder().title(title).content(content).author(author).build());
        String url = "/api/v1/posts";

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(title))
                .andExpect(jsonPath("$[0].content").value(content));
    }

    @Test
    public void 생성하기() throws Exception {
        // given
        PostDto request = PostDto.builder().title(title).content(content).build();
        String url = "/api/v1/posts";

        // when
        ResultActions actions = mvc.perform(post(url).sessionAttr("member", new SessionMember(email, name))
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isCreated());
        Post post = postRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(post.getCreatedAt()).isBefore(LocalDateTime.now());
        assertThat(post.getModifiedAt()).isBefore(LocalDateTime.now());
    }

    @Test
    public void 필수값_누락에_의한_생성하기_실패() throws Exception {
        // given
        PostDto request = PostDto.builder().content(content).build();
        String url = "/api/v1/posts";

        // when
        ResultActions actions = mvc.perform(post(url).sessionAttr("member", new SessionMember(email, name))
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 비로그인에_의한_생성하기_실패() throws Exception {
        // given
        PostDto request = PostDto.builder().title(title).content(content).build();
        String url = "/api/v1/posts";

        // when
        ResultActions actions = mvc.perform(post(url)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isUnauthorized());
    }

    @Test
    public void 존재하지_않는_계정에_의한_생성하기_실패() throws Exception {
        // given
        PostDto request = PostDto.builder().title(title).content(content).build();
        String url = "/api/v1/posts";

        // when
        ResultActions actions = mvc.perform(post(url).sessionAttr("member", new SessionMember("invalid", "name"))
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 제목으로_검색하기() throws Exception {
        // given
        postRepository.save(Post.builder().title(title).content(content).author(author).build());
        String url = "/api/v1/posts?type=title&keyword=Hello";

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(title))
                .andExpect(jsonPath("$[0].content").value(content));
    }

    @Test
    public void 작성자로_검색하기() throws Exception {
        // given
        postRepository.save(Post.builder().title(title).content(content).author(author).build());
        String url = "/api/v1/posts?type=author&keyword=Ted";

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(title))
                .andExpect(jsonPath("$[0].content").value(content));
    }

    @Test
    public void 잘못된_검색조건으로_검색하기_실패() throws Exception {
        // given
        postRepository.save(Post.builder().title(title).content(content).author(author).build());
        String url = "/api/v1/posts?type=invalid&keyword=Ted";

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions
                .andExpect(status().isBadRequest());
    }

    @Test
    public void 게시글_조회하기() throws Exception {
        // given
        Long id = postRepository.save(Post.builder().title(title).content(content).author(author).build()).getId();
        String url = "/api/v1/posts/" + id;

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value(title))
                .andExpect(jsonPath("$.content").value(content));
    }

    @Test
    public void 존재하지_않는_게시글_조회에_의한_게시글_조회하기_실패() throws Exception {
        // given
        String url = "/api/v1/posts/" + -1;

        // when
        ResultActions actions = mvc.perform(get(url));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 수정하기() throws Exception {
        // given
        String modifyTitle = "modify title";
        String modifyContent = "modify content";
        Long id = postRepository.save(Post.builder().title(title).content(content).author(author).build()).getId();
        PostDto request = PostDto.builder().title(modifyTitle).content(modifyContent).build();
        String url = "/api/v1/posts/" + id;

        // when
        ResultActions actions = mvc.perform(put(url)
                        .sessionAttr("member", new SessionMember(email, name))
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        Post post = postRepository.findById(id).get();
        assertThat(post.getTitle()).isEqualTo(modifyTitle);
        assertThat(post.getContent()).isEqualTo(modifyContent);
        assertThat(post.getModifiedAt()).isAfter(post.getCreatedAt());
    }

    @Test
    public void 필수값_누락에_의한_수정하기_실패() throws Exception {
        // given
        String modifyTitle = "modify title";
        String modifyContent = "modify content";
        Long id = postRepository.save(Post.builder().title(title).content(content).author(author).build()).getId();
        PostDto request = PostDto.builder().content(modifyContent).build();
        String url = "/api/v1/posts/" + id;

        // when
        ResultActions actions = mvc.perform(put(url)
                .sessionAttr("member", new SessionMember(email, name))
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 비로그인에_의한_수정하기_실패() throws Exception {
        // given
        String modifyTitle = "modify title";
        String modifyContent = "modify content";
        Long id = postRepository.save(Post.builder().title(title).content(content).author(author).build()).getId();
        PostDto request = PostDto.builder().title(modifyTitle).content(modifyContent).build();
        String url = "/api/v1/posts/" + id;

        // when
        ResultActions actions = mvc.perform(put(url)
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isUnauthorized());
    }

    @Test
    public void 존재하지_않는_계정에_의한_수정하기_실패() throws Exception {
        // given
        String modifyTitle = "modify title";
        String modifyContent = "modify content";
        Long id = postRepository.save(Post.builder().title(title).content(content).author(author).build()).getId();
        PostDto request = PostDto.builder().title(modifyTitle).content(modifyContent).build();
        String url = "/api/v1/posts/" + id;

        // when
        ResultActions actions = mvc.perform(put(url).sessionAttr("member", new SessionMember("invalid email", name))
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 존재하지_않는_게시글에_의한_수정하기_실패() throws Exception {
        // given
        String modifyTitle = "modify title";
        String modifyContent = "modify content";
        Long id = postRepository.save(Post.builder().title(title).content(content).author(author).build()).getId();
        PostDto request = PostDto.builder().title(modifyTitle).content(modifyContent).build();
        String url = "/api/v1/posts/" + -1;

        // when
        ResultActions actions = mvc.perform(put(url).sessionAttr("member", new SessionMember(email, name))
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 현재_사용자와_게시글_작성자_불일치에_의한_수정하기_실패() throws Exception {
        // given
        String email2 = "email2";
        String name2 = "name2";
        memberRepository.save(Member.builder().email(email2).password(password).name(name2).phone(phone).build());
        String modifyTitle = "modify title";
        String modifyContent = "modify content";
        Long id = postRepository.save(Post.builder().title(title).content(content).author(author).build()).getId();
        PostDto request = PostDto.builder().title(modifyTitle).content(modifyContent).build();
        String url = "/api/v1/posts/" + id;

        // when
        ResultActions actions = mvc.perform(put(url).sessionAttr("member", new SessionMember(email2, name2))
                .contentType("application/json")
                .content(new ObjectMapper().writeValueAsString(request)));

        // then
        actions.andExpect(status().isUnauthorized());
    }

    @Test
    public void 삭제하기() throws Exception {
        // given
        Long id = postRepository.save(Post.builder().title(title).content(content).author(author).build()).getId();
        String url = "/api/v1/posts/" + id;

        // when
        ResultActions actions = mvc.perform(delete(url).sessionAttr("member", new SessionMember(email, name)));

        // given
        actions.andExpect(status().isOk());
        List<Post> posts = postRepository.findAll();
        assertThat(posts.size()).isEqualTo(0);
    }

    @Test
    public void 비로그인에_의한_삭제하기_실패() throws Exception {
        // given
        Long id = postRepository.save(Post.builder().title(title).content(content).author(author).build()).getId();
        String url = "/api/v1/posts/" + id;

        // when
        ResultActions actions = mvc.perform(delete(url));

        // given
        actions.andExpect(status().isUnauthorized());
    }

    @Test
    public void 존재하지_않는_계정에_의한_삭제하기_실패() throws Exception {
        // given
        Long id = postRepository.save(Post.builder().title(title).content(content).author(author).build()).getId();
        String url = "/api/v1/posts/" + id;

        // when
        ResultActions actions = mvc.perform(delete(url).sessionAttr("member", new SessionMember("invalid", name)));

        // given
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 존재하지_않는_게시글에_의한_삭제하기_실패() throws Exception {
        // given
        Long id = postRepository.save(Post.builder().title(title).content(content).author(author).build()).getId();
        String url = "/api/v1/posts/" + -1;

        // when
        ResultActions actions = mvc.perform(delete(url).sessionAttr("member", new SessionMember(email, name)));

        // given
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 현재_사용자와_게시글_작성자_불일치에_의한_삭제하기_실패() throws Exception {
        // given
        String email2 = "email2";
        String name2 = "name2";
        memberRepository.save(Member.builder().email(email2).password(password).name(name2).phone(phone).build());
        Long id = postRepository.save(Post.builder().title(title).content(content).author(author).build()).getId();
        String url = "/api/v1/posts/" + id;

        // when
        ResultActions actions = mvc.perform(delete(url).sessionAttr("member", new SessionMember(email2, name2)));

        // given
        actions.andExpect(status().isUnauthorized());
    }
}
