package com.jang.gitminiprojectthejoeun.controller;

import com.jang.gitminiprojectthejoeun.dao.MemberDao;
import com.jang.gitminiprojectthejoeun.dao.BoardDao;
import com.jang.gitminiprojectthejoeun.dto.BoardDto;
import com.jang.gitminiprojectthejoeun.dto.LoginDto;
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
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberDao memberDao;
    private final BoardDao boardDao;

    // 회원가입 폼
    @GetMapping("/signup")
    public String signup(Model model,
                         @ModelAttribute("memberDto") MemberDto memberDto) {
        // FlashAttribute로 넘어온 memberDto가 있으면 그대로 사용
        if (!model.containsAttribute("memberDto")) {
            model.addAttribute("memberDto", new MemberDto());
        }
        return "member/signup";
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute MemberDto memberDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (bindingResult.hasErrors()) {
            return "member/signup";
        }

        try {
            // ✅ 중복 체크
            int duplicateUserID = memberDao.existsUserId(memberDto.getUserID());
            int duplicateUserEmail = memberDao.existsEmail(memberDto.getUserEmail());

            if (duplicateUserID > 0) {
                redirectAttributes.addFlashAttribute("msg", "이미 사용 중인 아이디입니다 ❌");
                redirectAttributes.addFlashAttribute("memberDto", memberDto); // ✅ 입력값 유지
                return "redirect:/member/signup";
            }

            if (duplicateUserEmail > 0) {
                redirectAttributes.addFlashAttribute("msg", "이미 사용 중인 이메일입니다 ❌");
                redirectAttributes.addFlashAttribute("memberDto", memberDto); // ✅ 입력값 유지
                return "redirect:/member/signup";
            }

            // ✅ 비밀번호 일치 확인
            if (!memberDto.getUserPW().equals(memberDto.getUserPWConfirm())) {
                redirectAttributes.addFlashAttribute("msg", "패스워드가 일치하지 않습니다 ❌");
                redirectAttributes.addFlashAttribute("memberDto", memberDto);
                return "redirect:/member/signup";
            }

            // ✅ DB 등록
            int result = memberDao.signup(memberDto);
            if (result > 0) {
                redirectAttributes.addFlashAttribute("msg", "회원가입이 완료되었습니다 🎉");
                return "redirect:/index";
            } else {
                redirectAttributes.addFlashAttribute("msg", "회원가입 중 문제가 발생했습니다 ❌");
                redirectAttributes.addFlashAttribute("memberDto", memberDto);
                return "redirect:/member/signup";
            }

        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("msg", "이미 존재하는 아이디 또는 이메일입니다 ❌");
            redirectAttributes.addFlashAttribute("memberDto", memberDto);
            return "redirect:/member/signup";
        }
    }

    // 아이디 중복체크 (AJAX)
    @PostMapping("/idCheck")
    @ResponseBody
    public Map<String, Boolean> idCheck(@RequestBody MemberDto memberDto) {
        int result = memberDao.existsUserId(memberDto.getUserID());
        Map<String, Boolean> map = new HashMap<>();
        map.put("isDuplicate", result > 0);
        return map;
    }

    // 로그인 폼
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginDto", new LoginDto());
        return "member/login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String loginProcess(@Valid @ModelAttribute LoginDto loginDto,
                               BindingResult bindingResult,
                               HttpSession session,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        if (bindingResult.hasErrors()) {
            return "member/login";
        }

        // 1️⃣ 아이디 존재 여부 확인
        int idExists = memberDao.existsUserId(loginDto.getUserID());
        if (idExists == 0) {
            redirectAttributes.addFlashAttribute("msg", "아이디가 틀립니다 ❌");
            return "redirect:/member/login";
        }

        // 2️⃣ 로그인 시도 (아이디는 존재함)
        MemberDto loggedMemberDto = memberDao.login(loginDto);
        if (loggedMemberDto == null) {
            redirectAttributes.addFlashAttribute("msg", "비밀번호가 틀립니다 ❌");
            return "redirect:/member/login";
        }

        // 3️⃣ 로그인 성공
        session.setAttribute("loggedMember", loggedMemberDto);
        redirectAttributes.addFlashAttribute("msg", "로그인 성공했습니다 🎉");
        return "redirect:/";
    }


    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("msg", "로그아웃 되었습니다 👋");
        return "redirect:/";
    }

    // 회원정보 페이지
    @GetMapping("/info")
    public String info(
            HttpSession session,
            @ModelAttribute("pageDto") PageDto pageDto,
            Model model) {

        MemberDto loggedMember = (MemberDto) session.getAttribute("loggedMember");
        if (loggedMember == null) {
            return "redirect:/member/login";
        }

        int memberId = loggedMember.getId();
        int page = pageDto.getPage();
        int size = pageDto.getSize();

        // ✅ 전체 게시글 수
        int totalPosts = boardDao.countMyPosts(memberId);
        int totalPages = (int) Math.ceil((double) totalPosts / size);
        int offset = (page - 1) * size;

        // ✅ 페이지 관련 정보 세팅
        pageDto.setOffset(offset);
        pageDto.setTotal(totalPosts);
        pageDto.setTotalPages(totalPages);
        pageDto.setHasPrev(page > 1);
        pageDto.setHasNext(page < totalPages);

        // ✅ 내가 쓴 글 목록 조회
        List<BoardDto> myBoardList = boardDao.findByMemberId(memberId, offset, size);

        model.addAttribute("member", loggedMember);
        model.addAttribute("myBoardList", myBoardList);
        model.addAttribute("responsePageDto", pageDto);

        return "member/info";
    }


    @GetMapping("/update")
    public String updateForm(HttpSession session, Model model, RedirectAttributes ra) {
        MemberDto logged = (MemberDto) session.getAttribute("loggedMember");
        if (logged == null) {
            ra.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }
        MemberDto memberDto = memberDao.findByUserId(logged.getUserID());
        model.addAttribute("memberDto", memberDto);
        return "member/detail-update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute MemberDto memberDto,
                         HttpSession session,
                         RedirectAttributes ra) {
        MemberDto logged = (MemberDto) session.getAttribute("loggedMember");
        if (logged == null) {
            ra.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        memberDto.setId(logged.getId());

        try {
            int updated = memberDao.updateMember(memberDto);
            if (updated > 0) {
                session.setAttribute("loggedMember", memberDao.findByUserId(logged.getUserID()));
                ra.addFlashAttribute("msg", "회원정보가 수정되었습니다 ✅");
            } else {
                ra.addFlashAttribute("msg", "수정에 실패했습니다 ❌");
            }
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            ra.addFlashAttribute("msg", "이미 사용 중인 이메일입니다 ❌");
        }

        return "redirect:/member/info";
    }


    // 회원삭제 폼
    @GetMapping("/delete")
    public String deleteForm(HttpSession session, RedirectAttributes ra) {
        MemberDto loggedMember = (MemberDto) session.getAttribute("loggedMember");
        if (loggedMember == null) {
            ra.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }
        return "member/delete";
    }

    // 회원삭제 처리
    @PostMapping("/delete")
    public String deleteProcess(@RequestParam("userPW") String userPW,
                                HttpSession session,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        MemberDto memberDto = (MemberDto) session.getAttribute("loggedMember");
        if (memberDto == null) {
            redirectAttributes.addFlashAttribute("msg", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }

        LoginDto loginDto = new LoginDto();
        loginDto.setUserID(memberDto.getUserID());
        loginDto.setUserPW(userPW);

        int result = memberDao.deleteMember(loginDto);
        if (result > 0) {
            session.invalidate();
            redirectAttributes.addFlashAttribute("msg", "회원 탈퇴가 완료되었습니다 👋");
            return "redirect:/";
        }

        model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
        return "member/delete";
    }
}
