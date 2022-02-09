package com.study.boardservice.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.title LIKE %?1% ORDER BY p.id DESC")
    List<Post> findAllDescByTitleLike(String title);

    @Query("SELECT p FROM Post p WHERE p.author.name LIKE %?1% ORDER BY p.id DESC")
    List<Post> findAllDescByAuthorNameLike(String name);

    @Query("SELECT p FROM Post p ORDER BY p.id DESC")
    List<Post> findAllDesc();
}
