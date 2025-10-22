package com.jang.gitminiprojectthejoeun.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageDto {
    //세로관련
    private int page = 1;  //현재 페이지
    private int size = 10;  //몇개 보여줄건지..

    //가로관련
    int total;     //전체 게시물 갯수
    int totalPages; // 몇개의 페이지가 나올지
    private boolean hasNext;
    private boolean hasPrev;

    //검색관련
    private String keyword;
    private String type;

    //1~5  //6~10 //11~15 //16~20 //21~25 //26~30 //31
    //if   page  1~5  1
    public int getStartPage(int blockSize) {
        return ((page - 1) / blockSize)*blockSize+1;
    }
    public int getEndPage(int blockSize) {
        int endPage = getStartPage(blockSize)+blockSize - 1;
        return Math.min(endPage, totalPages);
    }

    // 페이징용 offset
    private int offset;
}
