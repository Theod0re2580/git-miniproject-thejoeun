package com.jang.gitminiprojectthejoeun.dao;

import com.jang.gitminiprojectthejoeun.dto.LoginDto;
import com.jang.gitminiprojectthejoeun.dto.MemberDto;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberDao {
    int signup(MemberDto memberDto);
    int idCheck(MemberDto memberDto);
    int existsUserId(String userID);
    int existsEmail(String userEmail);
    MemberDto login(LoginDto loginDto);
    int deleteMember(LoginDto loginDto);
}
