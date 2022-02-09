package com.study.boardservice.domain.post;

import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.member.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class PostRepositoryTests {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;

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
    @Transactional
    public void 생성하기(){
        // given
        Post post = Post.builder().title(title).content(content).author(author).build();

        // when
        postRepository.save(post);

        // then
        Post savedPost = postRepository.findAll().get(0);
        assertThat(savedPost.getTitle()).isEqualTo(title);
        assertThat(savedPost.getContent()).isEqualTo(content);
        assertThat(savedPost.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(savedPost.getCreatedAt()).isBefore(LocalDateTime.now());
        assertThat(savedPost.getModifiedAt()).isBefore(LocalDateTime.now());
    }

    @Test
    public void 내림차순으로_조회하기(){
        // given
        postRepository.save(Post.builder().title(title).content(content).author(author).build());
        postRepository.save(Post.builder().title(title).content(content).author(author).build());

        // when
        List<Post> posts = postRepository.findAllDesc();

        // then
        assertThat(posts.get(0).getId()).isGreaterThan(posts.get(1).getId());
    }

    @Transactional
    @Test
    public void postId로_조회하기(){
        // given
        Long id = postRepository.save(Post.builder().title(title).content(content).author(author).build()).getId();

        // when
        Post post = postRepository.findById(id).get();

        // then
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(post.getCreatedAt()).isBefore(LocalDateTime.now());
        assertThat(post.getModifiedAt()).isBefore(LocalDateTime.now());
    }

    @Transactional
    @Test
    public void 내림차순으로_title_조회하기(){
        // given
        postRepository.save(Post.builder().title(title).content(content).author(author).build());

        // when
        List<Post> posts = postRepository.findAllDescByTitleLike(title);

        // then
        Post post = posts.get(0);
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(post.getCreatedAt()).isBefore(LocalDateTime.now());
        assertThat(post.getModifiedAt()).isBefore(LocalDateTime.now());
    }

    @Transactional
    @Test
    public void 내치람차순으로_author_조회하기(){
        // given
        postRepository.save(Post.builder().title(title).content(content).author(author).build());

        // when
        List<Post> posts = postRepository.findAllDescByAuthorNameLike(author.getName());

        // then
        Post post = posts.get(0);
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(post.getCreatedAt()).isBefore(LocalDateTime.now());
        assertThat(post.getModifiedAt()).isBefore(LocalDateTime.now());
    }

    @Test
    public void 수정하기() {
        // given
        Post post = postRepository.save(Post.builder().title(title).content(content).author(author).build());
        post.modify(title, content);

        // when
        postRepository.save(post);

        // then
        Post modifiedPost = postRepository.findAll().get(0);
        assertThat(modifiedPost.getTitle()).isEqualTo(title);
        assertThat(modifiedPost.getContent()).isEqualTo(content);
        assertThat(modifiedPost.getModifiedAt()).isAfter(modifiedPost.getCreatedAt());
    }

    @Test
    public void 삭제하기(){
        // given
        Long id = postRepository.save(Post.builder().title(title).content(content).author(author).build()).getId();

        // when
        postRepository.deleteById(id);

        // then
        List<Post> posts = postRepository.findAll();
        assertThat(posts.size()).isEqualTo(0);
    }
}
