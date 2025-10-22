package com.jang.gitminiprojectthejoeun.dao;

import com.jang.gitminiprojectthejoeun.dto.LoginDto;
import com.jang.gitminiprojectthejoeun.dto.MemberDto;
import jakarta.validation.Valid;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberDao {
    int signup(MemberDto memberDto);
    int idCheck(MemberDto memberDto);
    int existsUserId(String userID);
    int existsEmail(String userEmail);
    MemberDto login(LoginDto loginDto);
    int deleteMember(LoginDto loginDto);

    // ✅ 추가: userid로 회원 조회
    MemberDto findByUserId(@Param("userID") String userID);

    // ✅ 추가: 회원정보 수정 (board의 update처럼)
    int updateMember(MemberDto memberDto);
}
