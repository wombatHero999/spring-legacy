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
	@PostMapping("/security/insert") // 회원가입
	
	// @PostMapping("/security/login") // 인증 후 제거.
	
	
	@GetMapping("/security/myPage")
	public String myPage(Authentication authentication, Model model, Principal principal) {
		// 인증된 사용자 정보 가져오기
		// 1. SecurityContextHolder에 저장된 인증정보 가져오기
		//   - 스레드로컬에 저장되어있다가 세션에 할당.
		//   - 세션에서 꺼내오기?
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Member principal = (Member) authentication.getPrincipal();
		model.addAttribute("loginUser", principal);
		
		Member principal = (Member) authentication.getPrincipal();
		model.addAttribute("loginUser", principal);
		
		// 2. HandlerMapping으로 가져오기
		//  - Principal, Authentication할당

		log.debug("authentication = {}", authentication);
		// authentication = org.springframework.security.authentication.UsernamePasswordAuthenticationToken@23abe407: Principal: Member(id=honggd, password=$2a$10$qHHeJGgQ9teamJyIJFXbyOBtl7nIsQ37VP2jrz89dnDA7LgzS.nYi, name=카길동, gender=M, birthday=2021-05-04, email=honggd@naver.com, phone=01012341234, address=서울시 강남구, hobby=[운동,  등산], enrollDate=2021-05-20, authorities=[ROLE_USER], enabled=true); Credentials: [PROTECTED]; Authenticated: true; Details: org.springframework.security.web.authentication.WebAuthenticationDetails@166c8: RemoteIpAddress: 0:0:0:0:0:0:0:1; SessionId: B95C1041773474D93729781512D4490A; Granted Authorities: ROLE_USER
		log.debug("principal = {}", principal);
		return "member/myPage";
	}
	@PostMapping("/security/update")
	public String memberUpdate(
			Member updateMember,
			Authentication oldAuthentication, // 로그인한 사용자의 인증정보
			RedirectAttributes redirectAttr) {
		log.debug("updateMember = {}", updateMember); // password, authorites누락
		//1. 업무로직 : db의 member객체를 수정 ==> 생략.
		
		//2.변경된 회원정보를 DB에서 얻어 온 후 session에 저장하는 대신에 
		// SecurityContext의 authentication객체를 수정
		//updateMember에 누락된 정보 password, authorities 추가
		updateMember.setUserPwd(((Member) oldAuthentication.getPrincipal()).getPassword());
		
		Collection<? extends GrantedAuthority> oldAuthorities = 
				oldAuthentication.getAuthorities(); // 로그인한 사용자의 권한목록들(ADMIN, USER)
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
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
	
	/**
	 * 커맨드객체 이용시 사용자입력값(String)을 특정필드타입으로 변환할 editor객체를 설정
	 * 
	 * @param binder
	 */
//	@InitBinder
//	public void initBinder(WebDataBinder binder) {
//		//Member.birthday:java.sql.Date 타입 처리
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		
//		//커스텀에디터 생성 : allowEmpty - true (빈문자열을 null로 변환처리 허용)
//		PropertyEditor editor = new CustomDateEditor(sdf, true);
//		binder.registerCustomEditor(java.sql.Date.class, editor);
//	}
	
	
  @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new MemberValidator());
    }

    @PostMapping("/register")
    public String register(
        @ModelAttribute Member member,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "member/registerForm";
        }
        // 성공 처리
        return "redirect:/login";
    }
	
	
}
