package com.jang.gitminiprojectthejoeun.controller;

import com.jang.gitminiprojectthejoeun.dao.BoardDao;
import com.jang.gitminiprojectthejoeun.dto.BoardDto;
import com.jang.gitminiprojectthejoeun.dto.MemberDto;
import com.jang.gitminiprojectthejoeun.dto.PageDto;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardDao boardDao;

    @GetMapping("/list")
    public String list(Model model,
                       @ModelAttribute("pageDto")  PageDto pageDto
    )
    {
        int page =  pageDto.getPage();
        int size =  pageDto.getSize();
        int totalBoard =  boardDao.totalBoard(pageDto); //전체 게시물 수  33 /10
        int totalPages =  (int)Math.ceil((double)totalBoard/size);
        if(totalBoard==0) {
            model.addAttribute("boardList",List.of());
            PageDto responsePageDto = PageDto.builder()
                    .page(page)
                    .size(size)
                    .keyword(pageDto.getKeyword())
                    .type(pageDto.getType())
                    .total(totalBoard)
                    .totalPages(1)
                    .hasPrev(false)
                    .hasNext(false)
                    .build();
            model.addAttribute("responsePageDto",responsePageDto);
            return "/board/list";
        }
        if(page < 1) {
            page = 1;
            return "redirect:/board/list?page="+page+"&size="+size;
        }  //0보다 작아지지 않게....
        if(page > totalPages) {
            page = totalPages;
            return "redirect:/board/list?page="+page+"&size="+size;
        } // 마지막 보다 커지지 않게...
        int currentPage = (page-1)*size;
        System.out.println("pageDto==="+pageDto);
        List<BoardDto> boardList = boardDao.findAll(pageDto);
        System.out.println("페이지 = "+boardList.size());
        model.addAttribute("boardList", boardList);
        PageDto responsePageDto = PageDto.builder()
                .page(page)
                .size(size)
                .keyword(pageDto.getKeyword())
                .type(pageDto.getType())
                .total(totalBoard)
                .totalPages(totalPages)
                .hasPrev(page>1)
                .hasNext(page<totalPages)
                .build();

        model.addAttribute("responsePageDto",responsePageDto);
        return "board/list";
    }

    @GetMapping("/write")
    public String write(Model model, HttpSession session) {
        //로그인한 사용자면 이름을 넣어서 넘겨주고 아니면 빈 dto내려보내기
        MemberDto loggedMember = (MemberDto)session.getAttribute("loggedMember");
        BoardDto boardDto = new BoardDto();
        if(loggedMember!=null){
            boardDto.setWriter(loggedMember.getUserName());
        }
        model.addAttribute("boardDto", boardDto);
        return "board/write";
    }
    @PostMapping("/write")
    public String writeProcess(@Valid BoardDto boardDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "board/write";
        }
        System.out.println(boardDto);
        int result = boardDao.writeBoard(boardDto);
        if(result > 0) {
            return "redirect:/board/list";
        }
        return "board/write";
    }
    @GetMapping("/{id}/detail")
    public String write(@PathVariable("id") int id, Model model) {
        BoardDto boardDto = boardDao.findById(id);
        model.addAttribute("boardDto", boardDto);
        return "board/detail";
    }
    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Boolean> delete(@RequestBody BoardDto boardDto) {
        System.out.println("boardDto==="+boardDto);
        int result = boardDao.deleteBoard(boardDto);
        Map<String, Boolean> map = new HashMap<>();

        if(result > 0) {
            map.put("success", true);
        } else {
            map.put("success", false);
        }
        return map;
    }
    @GetMapping("/search")
    public String search(
            @RequestParam(value = "keyword",defaultValue = "") String keyword,
            @RequestParam(value = "type",defaultValue = "title") String type,
            Model model) {
        System.out.println("keyword==="+keyword);
        List<BoardDto> searchList = boardDao.search(keyword, type);
        model.addAttribute("searchList", searchList);
        return "board/search-list";
    }
}