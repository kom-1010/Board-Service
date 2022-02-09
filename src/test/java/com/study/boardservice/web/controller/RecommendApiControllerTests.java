package com.study.boardservice.web.controller;

import com.study.boardservice.domain.like.Recommend;
import com.study.boardservice.domain.like.RecommendRepository;
import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.member.MemberRepository;
import com.study.boardservice.domain.post.Post;
import com.study.boardservice.domain.post.PostRepository;
import com.study.boardservice.web.dto.SessionMember;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class RecommendApiControllerTests {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private RecommendRepository recommendRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;

    private Member member;
    private Post post;

    @BeforeEach
    public void setUp(){
        member = memberRepository.save(Member.builder().email("email").password("password").name("name").phone("phone").build());
        post = postRepository.save(Post.builder().title("title").content("content").author(member).build());
    }

    @AfterEach
    public void tearDown(){
        recommendRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    public void 추천하기() throws Exception {
        // given
        String url = "/api/v1/posts/" + post.getId() + "/recommend";

        // when
        ResultActions actions = mvc.perform(post(url).sessionAttr("member", new SessionMember("email", "name")));

        // then
        actions.andExpect(status().isCreated());
        Recommend recommend = recommendRepository.findAll().get(0);
        assertThat(recommend.getMember().getId()).isEqualTo(member.getId());
        assertThat(recommend.getPost().getId()).isEqualTo(post.getId());
    }

    @Test
    public void 존재하지_않는_게시글에_의한_추천하기_실패() throws Exception {
        // given
        String url = "/api/v1/posts/-1/recommend";

        // when
        ResultActions actions = mvc.perform(post(url).sessionAttr("member", new SessionMember("email", "name")));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 비로그인에_의한_추천하기_실패() throws Exception {
        // given
        String url = "/api/v1/posts/" + post.getId() + "/recommend";

        // when
        ResultActions actions = mvc.perform(post(url));

        // then
        actions.andExpect(status().isUnauthorized());
    }

    @Test
    public void 존재하지_않는_계정에_의한_추천하기_실패() throws Exception {
        // given
        String url = "/api/v1/posts/" + post.getId() + "/recommend";

        // when
        ResultActions actions = mvc.perform(post(url).sessionAttr("member", new SessionMember("invalid", "invalid")));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 중복추천에_의한_추천하기_실패() throws Exception {
        // given
        recommendRepository.save(Recommend.builder().member(member).post(post).build());
        String url = "/api/v1/posts/" + post.getId() + "/recommend";

        // when
        ResultActions actions = mvc.perform(post(url).sessionAttr("member", new SessionMember("email", "name")));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 추천취소하기() throws Exception {
        // given
        recommendRepository.save(Recommend.builder().member(member).post(post).build());
        String url = "/api/v1/posts/" + post.getId() + "/recommend";

        // when
        ResultActions actions = mvc.perform(delete(url)
                .sessionAttr("member", new SessionMember("email", "name")));

        // then
        actions.andExpect(status().isOk());
        List<Recommend> recommends = recommendRepository.findAll();
        assertThat(recommends.size()).isEqualTo(0);
    }

    @Test
    public void 존재하지_않은_게시글에_의한_추천취소하기_실패() throws Exception {
        // given
        recommendRepository.save(Recommend.builder().member(member).post(post).build());
        String url = "/api/v1/posts/-1/recommend";

        // when
        ResultActions actions = mvc.perform(delete(url)
                .sessionAttr("member", new SessionMember("email", "name")));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 비로그인에_의한_추천취소하기_실패() throws Exception {
        // given
        recommendRepository.save(Recommend.builder().member(member).post(post).build());
        String url = "/api/v1/posts/"+ post.getId() + "/recommend";

        // when
        ResultActions actions = mvc.perform(delete(url));

        // then
        actions.andExpect(status().isUnauthorized());
    }

    @Test
    public void 존재하지_않는_계정에_의한_추천취소하기_실패() throws Exception {
        // given
        recommendRepository.save(Recommend.builder().member(member).post(post).build());
        String url = "/api/v1/posts/"+ post.getId() + "/recommend";

        // when
        ResultActions actions = mvc.perform(delete(url)
                .sessionAttr("member", new SessionMember("invalid", "invalid")));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 존재하지_않는_추천에_대한_추천취소하기_실패() throws Exception {
        // given
        String url = "/api/v1/posts/"+ post.getId() + "/recommend";

        // when
        ResultActions actions = mvc.perform(delete(url)
                .sessionAttr("member", new SessionMember("email", "name")));

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 게시글_삭제에_의한_해당_게시글의_추천_삭제() throws Exception {
        // given
        Recommend recommend = recommendRepository.save(Recommend.builder().member(member).post(post).build());
        String url = "/api/v1/posts/" + post.getId();

        // when
        mvc.perform(delete(url).sessionAttr("member", new SessionMember("email", "name")));

        // then
        List<Recommend> recommends = recommendRepository.findAll();
        assertThat(recommends.size()).isEqualTo(0);
    }

    @Transactional
    @Test
    public void 게시글_추천애_의한_게시글_추천_수_증가() throws Exception {
        // given
        String url = "/api/v1/posts/" + post.getId() + "/recommend";

        // when
        mvc.perform(post(url).sessionAttr("member", new SessionMember("email", "name")));

        // then
        Post post = postRepository.findAll().get(0);
        Recommend recommend = recommendRepository.findAll().get(0);
        assertThat(recommend.getPost().getId()).isEqualTo(post.getId());
        assertThat(post.getRecommends().size()).isEqualTo(1);
    }

    @Test
    public void 게시글_추천_여부_조회하기() throws Exception {
        // given
        Recommend recommend = recommendRepository.save(Recommend.builder().member(member).post(post).build());
        String url = "/api/v1/posts/" + post.getId() + "/recommend";

        // when
        ResultActions actions = mvc.perform(get(url).sessionAttr("member", new SessionMember("email", "name")));

        // then
        actions.andExpect(status().isOk());
        actions.andExpect(jsonPath("$.recommended").value(true));
    }
}
