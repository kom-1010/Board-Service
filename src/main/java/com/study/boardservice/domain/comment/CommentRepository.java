package com.study.boardservice.domain.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);

    @Query("SELECT c FROM Comment c WHERE c.post.id = ?1 ORDER BY c.id DESC")
    List<Comment> findAllByPostIdDesc(Long postId);
}
