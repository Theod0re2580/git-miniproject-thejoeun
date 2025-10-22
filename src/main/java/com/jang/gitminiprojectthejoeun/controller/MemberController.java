package com.jang.gitminiprojectthejoeun.controller;

import com.jang.gitminiprojectthejoeun.dao.MemberDao;
import com.jang.gitminiprojectthejoeun.dto.LoginDto;
import com.jang.gitminiprojectthejoeun.dto.MemberDto;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberDao memberDao;

    // 회원가입 폼
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("memberDto", new MemberDto());
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

        int duplicateUserID = memberDao.existsUserId(memberDto.getUserID());
        int duplicateUserEmail = memberDao.existsEmail(memberDto.getUserEmail());

        if (duplicateUserID > 0) {
            bindingResult.rejectValue("userID", "duplicateID", "사용할 수 없는 ID입니다.");
            return "member/signup";
        }
        if (duplicateUserEmail > 0) {
            bindingResult.rejectValue("userEmail", "duplicateEmail", "사용할 수 없는 Email입니다.");
            return "member/signup";
        }
        if (!memberDto.getUserPW().equals(memberDto.getUserPWConfirm())) {
            bindingResult.rejectValue("userPWConfirm", "confirmPassword", "패스워드가 일치하지 않습니다.");
            return "member/signup";
        }

        int result = memberDao.signup(memberDto);
        if (result > 0) {
            redirectAttributes.addFlashAttribute("msg", "회원가입이 완료되었습니다 🎉");
            return "redirect:/index"; // ✅ 회원가입 후 홈으로 이동
        } else {
            model.addAttribute("error", "회원가입 중 문제가 발생했습니다.");
            return "member/signup";
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

        MemberDto loggedMemberDto = memberDao.login(loginDto);

        if (loggedMemberDto != null) {
            session.setAttribute("loggedMember", loggedMemberDto);
            redirectAttributes.addFlashAttribute("msg", "로그인 성공했습니다 🎉");
            return "redirect:/";
        } else {
            model.addAttribute("error", "아이디 또는 비밀번호가 틀렸습니다.");
            return "member/login";
        }
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
    public String info() {
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

        // 본인만 수정
        memberDto.setId(logged.getId());

        int updated = memberDao.updateMember(memberDto);
        if (updated > 0) {
            // 세션 최신화
            session.setAttribute("loggedMember", memberDao.findByUserId(logged.getUserID()));
            ra.addFlashAttribute("msg", "회원정보가 수정되었습니다 ✅");
        } else {
            ra.addFlashAttribute("msg", "수정에 실패했습니다 ❌");
        }
        return "redirect:/member/info";
    }


    // 회원삭제 폼
    @GetMapping("/delete")
    public String delete() {
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
