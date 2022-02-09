package com.study.boardservice.domain.like;

import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.member.MemberRepository;
import com.study.boardservice.domain.post.Post;
import com.study.boardservice.domain.post.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RecommendRepositoryTests {
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
    public void 생성하기(){
        // given
        Recommend recommend = Recommend.builder().member(member).post(post).build();

        // when
        recommendRepository.save(recommend);
        post.addRecommend(recommend);
        postRepository.save(post);

        // then
        Recommend createdRecommend = recommendRepository.findAll().get(0);
        assertThat(createdRecommend.getMember().getId()).isEqualTo(member.getId());
        assertThat(createdRecommend.getPost().getId()).isEqualTo(post.getId());
        assertThat(post.getRecommends().get(0).getId()).isEqualTo(recommend.getId());
    }

    @Test
    @Transactional
    public void 제거하기(){
        // given
        Recommend recommend = recommendRepository.save(Recommend.builder().member(member).post(post).build());
        post.addRecommend(recommend);
        postRepository.save(post);

        // when
        recommendRepository.deleteById(recommend.getId());
        post.removeRecommend(recommend);
        postRepository.save(post);

        // then
        List<Recommend> recommends = recommendRepository.findAll();
        assertThat(recommends.size()).isEqualTo(0);
        assertThat(post.getRecommends().size()).isEqualTo(0);
    }

    @Test
    @Transactional
    public void 게시글_제거에_의한_해당_게시글의_추천_제거(){
        // given
        Recommend recommend = recommendRepository.save(Recommend.builder().post(post).member(member).build());
        post.addRecommend(recommend);
        postRepository.save(post);

        // when
        postRepository.deleteById(post.getId());

        // then
        List<Recommend> all = recommendRepository.findAll();
        assertThat(all.size()).isEqualTo(0);
    }

    @Test
    public void postId와_memberId로_추천_확인하기(){
        // given
        Recommend recommend = recommendRepository.save(Recommend.builder().member(member).post(post).build());

        // when
        Boolean isRecommend = recommendRepository.existsByMemberIdAndPostId(member.getId(), post.getId());

        // then
        assertThat(isRecommend).isEqualTo(true);
    }

    @Test
    public void postId와_memberId로_추천_얻기(){
        // given
        Long id = recommendRepository.save(Recommend.builder().member(member).post(post).build()).getId();

        // when
        Recommend recommend = recommendRepository.findByMemberIdAndPostId(member.getId(), post.getId()).get();

        // then
        assertThat(recommend.getId()).isEqualTo(id);
    }
}
