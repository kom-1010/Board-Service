package com.study.boardservice.web.dto;

import com.study.boardservice.domain.member.Member;
import com.study.boardservice.exception.InvalidValueException;
import com.study.boardservice.exception.MissingEssentialValueException;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberDto {
    private String id;
    private String email;
    private String password;
    private String name;
    private String phone;
    private String newPassword;
    private String checkPassword;

    @Builder
    public MemberDto(String id, String email, String password, String name, String phone, String newPassword, String checkPassword){
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.newPassword = newPassword;
        this.checkPassword = checkPassword;
    }

    public Member toEntity() {
        return Member.builder().email(email).password(password).name(name).phone(phone).build();
    }

    public void validateSignupRequestDto(){
        if (email == null || email.equals(""))
            throw new MissingEssentialValueException("Email is empty");
        if (password == null || password.equals(""))
            throw new MissingEssentialValueException("Password is empty");
        if (name == null || name.equals(""))
            throw new MissingEssentialValueException("Name is empty");
        if (phone == null || phone.equals(""))
            throw new MissingEssentialValueException("Phone is empty");
    }

    public void validateLoginRequestDto(){
        if (email == null || email.equals(""))
            throw new MissingEssentialValueException("Email is empty");
        if (password == null || password.equals(""))
            throw new MissingEssentialValueException("Password is empty");
    }

    public void validateModifyPasswordRequestDto(){
        if (newPassword == null || newPassword.equals(""))
            throw new MissingEssentialValueException("New password is empty");
        if (checkPassword == null || checkPassword.equals(""))
            throw new MissingEssentialValueException("Check password is empty");
    }

    public void matchPasswords(){
        if (!newPassword.equals(checkPassword))
            throw new InvalidValueException("Password are not match");
    }
}
