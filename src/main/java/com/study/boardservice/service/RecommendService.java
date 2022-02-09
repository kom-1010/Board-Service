package com.study.boardservice.service;

import com.study.boardservice.domain.like.Recommend;
import com.study.boardservice.domain.like.RecommendRepository;
import com.study.boardservice.domain.member.Member;
import com.study.boardservice.domain.post.Post;
import com.study.boardservice.domain.post.PostRepository;
import com.study.boardservice.exception.DuplicateValueException;
import com.study.boardservice.exception.NotFoundValueException;
import com.study.boardservice.web.dto.RecommendDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RecommendService {
    private final RecommendRepository recommendRepository;
    private final PostRepository postRepository;

    @Transactional
    public void save(Member author, Post post) {
        // 이미 추천한 게시글에 중복으로 추천할 수 없음
        if (recommendRepository.existsByMemberIdAndPostId(author.getId(), post.getId()))
            throw new DuplicateValueException("이미 추천한 게시글입니다.");

        Recommend recommend = Recommend.builder().member(author).post(post).build();
        recommendRepository.save(recommend);

        // 게시글에 추천 내역 추가
        post.addRecommend(recommend);
    }

    @Transactional
    public void remove(Member author, Post post) {
        Recommend recommend = recommendRepository.findByMemberIdAndPostId(author.getId(), post.getId()).orElseThrow(() ->
                new NotFoundValueException("해당 추천이 존재하지 않습니다."));
        recommendRepository.deleteById(recommend.getId());
    }

    @Transactional
    public Boolean exist(Member author, Post post) {
        return recommendRepository.existsByMemberIdAndPostId(author.getId(), post.getId());
    }
}
