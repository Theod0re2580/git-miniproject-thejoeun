package com.jang.gitminiprojectthejoeun.dao;

import com.jang.gitminiprojectthejoeun.dto.BoardDto;
import com.jang.gitminiprojectthejoeun.dto.PageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardDao {
    int writeBoard(BoardDto boardDto);
    List<BoardDto> findAll(PageDto pageDto);

    BoardDto findById(int id);
    int deleteBoard(BoardDto boardDto);
    int totalBoard(PageDto pageDto);
    List<BoardDto> search(@Param("keyword") String keyword,
                          @Param("type") String type);
    BoardDto findPrev(int id);
    BoardDto findNext(int id);
    int updateBoard(BoardDto boardDto);
    String getSecretPw(int id);

    List<BoardDto> findByMemberId(@Param("memberId") int memberId,
                                  @Param("offset") int offset,
                                  @Param("size") int size);
    int countMyPosts(int memberId);
}
