package com.study.boardservice.domain.comment;

import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.member.MemberRepository;
import com.study.boardservice.domain.post.Post;
import com.study.boardservice.domain.post.PostRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.study.boardservice.config.TestValue.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CommentRepositoryTests {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;

    private Member author;
    private Post post;

    String commentContent = "test comment";

    @BeforeEach
    public void setUp(){
        author = memberRepository.save(Member.builder().email(EMAIL).password(PASSWORD).name(NAME).phone(PHONE).build());
        post = postRepository.save(Post.builder().title(TITLE).content(CONTENT).author(author).build());
    }

    @AfterEach
    public void tearDown(){
        commentRepository.deleteAll();
        postRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    public void 생성하기(){
        // given
        Comment comment = Comment.builder().content(commentContent).author(author).post(post).build();

        // when
        commentRepository.save(comment);

        // then
        Comment savedComment = commentRepository.findAll().get(0);
        assertThat(savedComment.getContent()).isEqualTo(commentContent);
        assertThat(savedComment.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(savedComment.getPost().getId()).isEqualTo(post.getId());
        assertThat(savedComment.getCreatedAt()).isBefore(LocalDateTime.now());
        assertThat(savedComment.getModifiedAt()).isBefore(LocalDateTime.now());
    }

    @Test
    public void 수정하기(){
        // given
        String modifiedContent = "modified content";
        Comment comment = commentRepository.save(Comment.builder().content(commentContent).author(author).post(post).build());
        comment.modify(modifiedContent);

        // when
        commentRepository.save(comment);

        // then
        Comment savedComment = commentRepository.findAll().get(0);
        assertThat(savedComment.getContent()).isEqualTo(modifiedContent);
        assertThat(savedComment.getModifiedAt()).isAfter(savedComment.getCreatedAt());
    }

    @Test
    public void 삭제하기(){
        // given
        Long id = commentRepository.save(Comment.builder().content(commentContent).author(author).post(post).build()).getId();

        // when
        commentRepository.deleteById(id);

        // then
        List<Comment> comments = commentRepository.findAll();
        assertThat(comments.size()).isEqualTo(0);
    }

    @Test
    public void 게시글_id로_조회하기(){
        // given
        Long postId = post.getId();
        Long commentId = commentRepository.save(Comment.builder().content(commentContent).author(author).post(post).build()).getId();

        // when
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        // then
        assertThat(comments.get(0).getId()).isEqualTo(commentId);
    }

    @Test
    public void 게시글_id로_내림차순_조회하기(){
        // given
        Long postId = post.getId();
        Long commentId = commentRepository.save(Comment.builder().content(commentContent).author(author).post(post).build()).getId();

        // when
        List<Comment> comments = commentRepository.findAllByPostIdDesc(postId);

        // then
        assertThat(comments.get(0).getId()).isEqualTo(commentId);
    }
}
