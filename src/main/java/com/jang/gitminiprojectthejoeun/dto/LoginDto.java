package com.jang.gitminiprojectthejoeun.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDto {


    @NotBlank(message = "아이디를 입력하세요.")
    private String userID;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
    private String userPW;


}
