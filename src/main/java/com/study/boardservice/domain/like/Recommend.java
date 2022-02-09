package com.study.boardservice.domain.like;

import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.post.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Recommend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Builder
    public Recommend(Member member, Post post){
        this.member = member;
        this.post = post;
    }

}
