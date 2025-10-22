package com.jang.gitminiprojectthejoeun.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private int id;
    private int memberId;  // 🔹 추가됨
    private String title;
    private String content;
    private String writer;  // ❗ 아직 화면용으로는 유지 가능
    private LocalDateTime regdate;
    private int hit;
    private String password;
    private int secretFlag; // 🔹 추가 가능
}