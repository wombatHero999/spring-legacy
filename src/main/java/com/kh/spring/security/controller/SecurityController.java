package com.kh.spring.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {
	
	/**
	 * Authentication
	 * 	- Principal : 인증된 사용자객체
	 *  - Credentials : 인증에 필요한 비밀번호에 대한 정보를 가진 객체. 
	 *                  내부적으로 인증작업시 필요하며 보호되고있음.
	 * 	- Authorities : 인증된 사용자가 가진 권한
	 */
	
	// 1. 로그인 / 로그아웃 
	//  - security-context.xml의 설정정보를 통해 처리.
	//  - /member/loginProcess로 요청이 들어오는 경우 Filter를 통해 인증을 처리
	
	@GetMapping("/security/accessDenied")
	public String accessDenied(Model model) {
		model.addAttribute("errorMsg","접근불가능한 페이지입니다.");
		return "common/errorPage";
	}
}
