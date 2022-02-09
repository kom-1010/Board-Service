package com.study.boardservice.web.dto;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionMember implements Serializable {
    private String email;
    private String name;

    public SessionMember(String email, String name){
        this.email = email;
        this.name = name;
    }
}
