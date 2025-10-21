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
    BoardDto getPrevBoard(int boardNo);
    BoardDto getNextBoard(int boardNo);
}
