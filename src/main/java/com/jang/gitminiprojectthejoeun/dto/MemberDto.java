package com.jang.gitminiprojectthejoeun.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto {

    private int id;

    @NotBlank(message = "아이디를 입력하세요.")
    private String userID;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
    private String userPW;

    private String userPWConfirm;

    @NotBlank(message = "이름을 입력하세요.")
    private String userName;

    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String userEmail;
}
