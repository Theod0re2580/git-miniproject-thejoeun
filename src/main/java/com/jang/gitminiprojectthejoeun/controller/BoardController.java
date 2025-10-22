package com.jang.gitminiprojectthejoeun.controller;

import com.jang.gitminiprojectthejoeun.dao.BoardDao;
import com.jang.gitminiprojectthejoeun.dto.BoardDto;
import com.jang.gitminiprojectthejoeun.dto.MemberDto;
import com.jang.gitminiprojectthejoeun.dto.PageDto;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardDao boardDao;

    @GetMapping("/list")
    public String list(Model model, @ModelAttribute("pageDto") PageDto pageDto) {

        // 🔹 검색 파라미터 출력 (디버깅용)
        System.out.println("검색 타입: " + pageDto.getType() + ", 키워드: " + pageDto.getKeyword());

        int page = pageDto.getPage();
        int size = pageDto.getSize();
        int totalBoard = boardDao.totalBoard(pageDto);
        int totalPages = (int) Math.ceil((double) totalBoard / size);

        // 🔹 결과가 없는 경우
        if (totalBoard == 0) {
            model.addAttribute("boardList", List.of());
            model.addAttribute("responsePageDto", PageDto.builder()
                    .page(page)
                    .size(size)
                    .total(totalBoard)
                    .totalPages(1)
                    .hasPrev(false)
                    .hasNext(false)
                    .keyword(pageDto.getKeyword())
                    .type(pageDto.getType())
                    .build());
            return "board/list";
        }

        // 🔹 페이지 유효성 검사
        if (page < 1)
            return "redirect:/board/list?page=1&size=" + size +
                    "&keyword=" + (pageDto.getKeyword() != null ? pageDto.getKeyword() : "") +
                    "&type=" + (pageDto.getType() != null ? pageDto.getType() : "all");

        if (page > totalPages)
            return "redirect:/board/list?page=" + totalPages + "&size=" + size +
                    "&keyword=" + (pageDto.getKeyword() != null ? pageDto.getKeyword() : "") +
                    "&type=" + (pageDto.getType() != null ? pageDto.getType() : "all");

        // 🔹 오프셋 계산 (페이징용)
        pageDto.setOffset((page - 1) * size);

        // 🔹 검색 + 페이징 조회
        List<BoardDto> boardList = boardDao.findAll(pageDto);

        // 🔹 페이지 정보 구성
        model.addAttribute("boardList", boardList);
        model.addAttribute("responsePageDto", PageDto.builder()
                .page(page)
                .size(size)
                .total(totalBoard)
                .totalPages(totalPages)
                .hasPrev(page > 1)
                .hasNext(page < totalPages)
                .keyword(pageDto.getKeyword())
                .type(pageDto.getType())
                .build());

        return "board/list";
    }

    /** ✅ 게시글 수정 폼 — 작성자만 접근 가능 */
    @GetMapping("/{id}/update")
    public String updateForm(@PathVariable("id") int id,
                             HttpSession session,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        MemberDto loggedMember = (MemberDto) session.getAttribute("loggedMember");
        if (loggedMember == null) {
            redirectAttributes.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        BoardDto boardDto = boardDao.findById(id);
        if (boardDto == null) {
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 게시글입니다.");
            return "redirect:/board/list";
        }

        // 작성자 본인만 접근 가능
        if (!boardDto.getWriter().equals(loggedMember.getUserName())) {
            redirectAttributes.addFlashAttribute("msg", "작성자만 수정할 수 있습니다.");
            return "redirect:/board/" + id + "/detail";
        }

        model.addAttribute("boardDto", boardDto);
        return "board/update";
    }

    /** ✅ 게시글 수정 처리 — 작성자 검증 포함 */
    @PostMapping("/update")
    public String updateProcess(@ModelAttribute("boardDto") BoardDto boardDto,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        MemberDto loggedMember = (MemberDto) session.getAttribute("loggedMember");
        if (loggedMember == null) {
            redirectAttributes.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        BoardDto original = boardDao.findById(boardDto.getId());
        if (original == null) {
            redirectAttributes.addFlashAttribute("msg", "게시글을 찾을 수 없습니다.");
            return "redirect:/board/list";
        }

        // ✅ 본인 확인
        if (original.getMemberId() != loggedMember.getId()) {
            redirectAttributes.addFlashAttribute("msg", "작성자만 수정할 수 있습니다.");
            return "redirect:/board/" + boardDto.getId() + "/detail";
        }

        // ✅ 업데이트 실행
        boardDto.setMemberId(loggedMember.getId());
        int result = boardDao.updateBoard(boardDto);

        if (result > 0) {
            redirectAttributes.addFlashAttribute("msg", "게시글이 수정되었습니다 ✅");
        } else {
            redirectAttributes.addFlashAttribute("msg", "수정 중 오류가 발생했습니다 ❌");
        }

        return "redirect:/board/" + boardDto.getId() + "/detail";
    }



    @GetMapping("/write")
    public String write(Model model, HttpSession session) {
        MemberDto loggedMember = (MemberDto) session.getAttribute("loggedMember");
        BoardDto boardDto = new BoardDto();
        if (loggedMember != null) {
            boardDto.setWriter(loggedMember.getUserName());
        }
        model.addAttribute("boardDto", boardDto);
        return "board/write";
    }

    @PostMapping("/write")
    public String writeProcess(@Valid BoardDto boardDto, BindingResult bindingResult,
                               HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "board/write";
        }

        MemberDto loggedMember = (MemberDto) session.getAttribute("loggedMember");
        if (loggedMember != null) {
            boardDto.setMemberId(loggedMember.getId());
            boardDto.setWriter(loggedMember.getUserName());
        }

        int result = boardDao.writeBoard(boardDto);
        return result > 0 ? "redirect:/board/list" : "board/write";
    }

    @GetMapping("/{id}/detail")
    public String detail(@PathVariable("id") int id, Model model, @ModelAttribute("msg") String msg) {
        BoardDto boardDto = boardDao.findById(id);
        BoardDto prevBoardDto = boardDao.findPrev(id);
        BoardDto nextBoardDto = boardDao.findNext(id);

        model.addAttribute("boardDto", boardDto);
        model.addAttribute("prevBoardDto", prevBoardDto);
        model.addAttribute("nextBoardDto", nextBoardDto);
        if (msg != null && !msg.isEmpty()) model.addAttribute("msg", msg);

        return "board/detail";
    }

    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> delete(@RequestBody BoardDto dto, HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        MemberDto loginUser = (MemberDto) session.getAttribute("loggedMember");
        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "로그인이 필요합니다.");
            return result;
        }

        dto.setWriter(loginUser.getUserID());
        int deleted = boardDao.deleteBoard(dto);

        if (deleted > 0) {
            result.put("success", true);
            result.put("message", "게시글이 삭제되었습니다.");
        } else {
            result.put("success", false);
            result.put("message", "비밀번호가 일치하지 않거나 작성자만 삭제할 수 있습니다.");
        }

        return result;
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                         @RequestParam(value = "type", defaultValue = "title") String type,
                         Model model) {
        List<BoardDto> searchList = boardDao.search(keyword, type);
        model.addAttribute("searchList", searchList);
        return "board/search-list";
    }
}
