package com.grepp.spring.app.controller.web.member;

import com.grepp.spring.app.controller.web.member.payload.SigninRequest;
import com.grepp.spring.app.controller.web.member.payload.SignupRequest;
import com.grepp.spring.app.model.auth.code.Role;
import com.grepp.spring.app.model.member.MemberService;
import com.grepp.spring.app.model.member.dto.MemberDto;
import com.grepp.spring.infra.error.exceptions.CommonException;
import com.grepp.spring.infra.response.ResponseCode;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("member")
public class MemberController {
    
    private final MemberService memberService;

    @GetMapping("signup")
    public String signup(SignupRequest form){
        return "member/signup";
    }
    
    @PostMapping("verify")
    public String verifySignup(
        @Valid SignupRequest form,
        BindingResult bindingResult,
        HttpSession session,
        RedirectAttributes redirectAttributes){
        
        if(bindingResult.hasErrors()){
            return "member/signup";
        }
        
        String token = UUID.randomUUID().toString();
        MemberDto dto = form.toDto();
        memberService.sendVerificationMail(token, dto);
        session.setAttribute(token, dto);
        redirectAttributes.addAttribute("msg", "회원가입 메일이 발송되었습니다.");
        return "redirect:/";
    }
    
    @GetMapping("signup/{token}")
    public String signup(
        @PathVariable
        String token,
        HttpSession session
       ){
        
        MemberDto dto = (MemberDto) session.getAttribute(token);
        
        if(dto == null){
            throw new CommonException(ResponseCode.INVALID_TOKEN);
        }
        
        memberService.signup(dto, Role.ROLE_USER);
        session.removeAttribute(token);
        return "redirect:/";
    }
    
    @GetMapping("signin")
    public String signin(SigninRequest form){
        return "member/signin";
    }
    
    
    @GetMapping("mypage")
    public String mypage(Authentication authentication, Model model){
        log.info("authentication : {}", authentication);
        String userId = authentication.getName();
        MemberDto memberDto = memberService.findById(userId);
        model.addAttribute("member", memberDto);
        return "member/mypage";
    }
    
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or authentication.name == #id")
    @GetMapping("{id}")
    public String get(@PathVariable String id, Model model){
        MemberDto memberDto = memberService.findById(id);
        model.addAttribute("member", memberDto);
        return "member/mypage";
    }
}
