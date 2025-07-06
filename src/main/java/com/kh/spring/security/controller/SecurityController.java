package com.kh.spring.security.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.validator.MemberValidator;
import com.kh.spring.member.model.vo.Member;
import com.kh.spring.security.model.vo.MemberExt;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class SecurityController {
	
	@GetMapping("/security/accessDenied")
	public String accessDenied(Model model) {
		model.addAttribute("errorMsg","접근불가능한 페이지입니다.");
		return "common/errorPage";
	}
	
	// 1. 로그인 / 로그아웃 
	//  - security-context.xml의 설정정보를 통해 처리.
	//  - /member/loginProcess로 요청이 들어오는 경우 Filter를 통해 인증을 처리
	/**
	 * 커맨드객체 이용시 사용자입력값(String)을 특정필드타입으로 변환할 editor객체를 설정
	 * 
	 * @param binder
	 */
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new MemberValidator());
    }
	
	// 1. 비밀번호 암호화
	// 2. 데이터 유효성 검사
	// 3. 실패시 결과값을 BindingResult에 담음.
	@PostMapping("/security/insert") 
    public String register(
        @ModelAttribute Member member,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "member/memberEnrollForm";
        }
        // 성공시 로그인 페이지로 이동
        return "redirect:/member/login";
    }
	
	/**
	 * Authentication
	 * 	- Principal : 인증된 사용자객체
	 *  - Credentials : 인증에 필요한 비밀번호에 대한 정보를 가진 객체. 
	 *                  내부적으로 인증작업시 필요하며 보호되고있음.
	 * 	- Authorities : 인증된 사용자가 가진 권한
	 */
	@GetMapping("/security/myPage")
	public String myPage(Authentication authenticationParam, Model model, Principal principalParam) {
		// 인증된 사용자 정보 가져오기
		// 1. SecurityContextHolder에 저장된 인증정보 가져오기
	    //   - SecurityContext는 스레드로컬(SecurityContextHolder)에 저장되며 세션에 바인딩됨.
		//   - 단, 시큐리티에서는 세션에서 직접 Security를 직접 꺼내는 것보다 스레드로컬에서 꺼내는것을 권장.
		//   - 시큐리티가 동작과정에서 ThreadLocal과 싱크하기때문
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Member principal = (MemberExt) authentication.getPrincipal();
		model.addAttribute("loginUser", principal);
		
	    // 2. HandlerMethodArgumentResolver로 매핑된 Authentication 파라미터 사용하기
	    //    (컨트롤러 메서드의 Authentication/Principal 파라미터에 자동 주입)
		 log.debug("authenticationParam = {}", authenticationParam);
	    log.debug("principalParam = {}", principalParam);

	    log.debug("authentication = {}", authentication);
	    log.debug("principal = {}", principal);
	    
		return "member/myPage";
	}
	
	@PostMapping("/security/update")
	public String memberUpdate(
			MemberExt updateMember,
			Authentication oldAuthentication, // 로그인한 사용자의 인증정보
			RedirectAttributes redirectAttr) {
		log.debug("updateMember = {}", updateMember); // password, authorites누락
		//1. 업무로직 : db의 member객체를 수정 ==> 생략.
		
		//2.변경된 회원정보를 DB에서 얻어 온 후 session에 저장하는 대신에 
		// SecurityContext의 authentication객체를 수정
		//updateMember에 누락된 정보 password, authorities 추가
		updateMember.setUserPwd(((MemberExt) oldAuthentication.getPrincipal()).getUserPwd());
		
		Collection<? extends GrantedAuthority> oldAuthorities = 
				oldAuthentication.getAuthorities(); // 로그인한 사용자의 권한목록들(ADMIN, USER)
		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		for(GrantedAuthority auth : oldAuthorities) {
			SimpleGrantedAuthority simpleAuth = 
					new SimpleGrantedAuthority(auth.getAuthority()); // 문자열을 인자로 auth객체생성 
			authorities.add(simpleAuth);
		}
		updateMember.setAuthorities(authorities);
		
		//새로운 authentication객체 생성
		Authentication newAuthentication = 
				new UsernamePasswordAuthenticationToken(
					updateMember,
					oldAuthentication.getCredentials(),
					oldAuthentication.getAuthorities()
				);
		//SecurityContextHolder - SecurityContext 하위에 설정
		SecurityContextHolder.getContext().setAuthentication(newAuthentication);
		
		//3. 사용자피드백 & 리다이렉트
		return "redirect:/security/myPage";
	}
	
	
	
	
    

    
	
	
}
