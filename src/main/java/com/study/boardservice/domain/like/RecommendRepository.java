package com.study.boardservice.domain.like;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    Boolean existsByMemberIdAndPostId(String memberId, Long postId);

    Optional<Recommend> findByMemberIdAndPostId(String memberId, Long postId);
}
