package com.study.boardservice.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Member> findByName(String name);

    Optional<Member> findByEmailAndName(String email, String name);
}
