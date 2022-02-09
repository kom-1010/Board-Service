package com.study.boardservice.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RecommendDto {
    private String memberId;
    private Long postId;

    @Builder
    public RecommendDto(String memberId, Long postId){
        this.memberId = memberId;
        this.postId = postId;
    }
}
