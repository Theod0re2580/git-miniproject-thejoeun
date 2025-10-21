package com.jang.gitminiprojectthejoeun.interceptor;

import com.jang.gitminiprojectthejoeun.dto.MemberDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        MemberDto loggedMember = null;
        if(session!=null){
            loggedMember = (MemberDto) session.getAttribute("loggedMember");
        }
        if(loggedMember==null) {
            response.sendRedirect("/member/login");
            return false;
        }
        return true;
    }
}
