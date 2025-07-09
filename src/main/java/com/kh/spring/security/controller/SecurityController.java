package com.kh.spring.security.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.validator.MemberValidator;
import com.kh.spring.member.model.vo.Member;
import com.kh.spring.security.model.vo.MemberExt;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class SecurityController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private MemberService mService;

	
	// 1. 로그인 / 로그아웃 
	//  - security-context.xml의 설정정보를 통해 처리.
	//  - /member/loginProcess로 요청이 들어오는 경우 Filter를 통해 인증을 처리
	
	// 에러페이지 처리용 url
	@RequestMapping("/security/accessDenied")
	public String accessDenied(Model model) {
		model.addAttribute("errorMsg","접근불가능한 페이지입니다.");
		return "common/errorPage";
	}
	
	/**
	 * InitBinder:
	 *  - 웹 데이터 바인더(WebDataBinder)
	 *  - 현재 컨트롤러에서 요청을 처리할 때, 커맨드 객체(예: @ModelAttribute Member)에 대한 바인딩 설정을 수행하는
	 *    객체
	 * 
	 * 처리 순서:
	 * 1) 클라이언트 요청 파라미터(String)를 커맨드 객체 필드에 바인딩
	 * 2) 필요한 경우, 타입 변환(Type Converter 또는 PropertyEditor)을 수행해 적절한 타입으로 변환
	 *  String형태의 문자열을 date타입으로, String형태의 문자열을 Integer로 변환을 수행
	 * 3) 등록된 Validator로 유효성 검사 실행
	 * 4) BindingResult에 검증 결과(에러) 저장
	 */
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new MemberValidator());
        
        // 날짜 문자열 -> Date 변환 포맷 지정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        dateFormat.setLenient(false); // yyMMdd형식이 아닌경우 에러 발생

        // CustomDateEditor 등록
        // birthday=991225와 같은 데이터가 들어오는 경우 수행
        binder.registerCustomEditor(Date.class, "birthday", new CustomDateEditor(dateFormat, true));
    }
	@GetMapping("/security/insert")
	public String enroll(@ModelAttribute Member member) {
		return "member/memberEnrollForm";
	}
	
	
	@PostMapping("/security/insert") 
	public String register(
			@Validated @ModelAttribute Member member
			// @ModelAttribue 
			//   - 커맨드객체 바인딩시 사용하며, 추가시 자동으로 model에 커맨드객체를 저장 
			, BindingResult bindingResult
			// BindingResult
			//  - 유효성 검사의 결과를 저장하는 객체
			//  - forward시 자동으로 전달되어 form데이터 바인딩에 에러내용을 바인딩하기 위해 사용한다
			) {
		// 유효성 검사 통과하지 못한 경우 에러데이터(Reject Value)를 담아 회원가입으로 포워딩
	    if (bindingResult.hasErrors()) {
	        return "member/memberEnrollForm";
	    }

	    // 비밀번호 암호화(단방향)
	    String encryptedPassword = passwordEncoder.encode(member.getUserPwd());
	    member.setUserPwd(encryptedPassword);

	    // 저장시 하나의 트랜잭션으로 회원+권한을 함께 저장. -> 로직구현 알아서하기
	    mService.insertMember(member);

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
			@ModelAttribute MemberExt loginUser, // form태그 바인딩을 위한 변수명 동기화
			 BindingResult bindingResult, // 유효성 결과 받기
			Authentication oldAuthentication, // 로그인한 사용자의 인증정보
			RedirectAttributes redirectAttr) {
		  // 유효성 검사 에러가 있으면 다시 폼으로 리다이렉트
	    if (bindingResult.hasErrors()) {
	        redirectAttr.addFlashAttribute("org.springframework.validation.BindingResult.updateMember", bindingResult);
	        redirectAttr.addFlashAttribute("loginUser", loginUser);
	        return "redirect:/security/myPage";
	    }
		
		log.debug("updateMember = {}", loginUser); // password, authorites누락
		//1. 업무로직 : db의 member객체를 수정 ==> 생략.
		
		//2.변경된 회원정보를 DB에서 얻어 온 후 session에 저장하는 대신에 
		// SecurityContext의 authentication객체를 수정
		//updateMember에 누락된 정보 password, authorities 추가
		loginUser.setUserPwd(((MemberExt) oldAuthentication.getPrincipal()).getUserPwd());
		
		Collection<? extends GrantedAuthority> oldAuthorities = 
				oldAuthentication.getAuthorities(); // 로그인한 사용자의 권한목록들(ADMIN, USER)
		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		for(GrantedAuthority auth : oldAuthorities) {
			SimpleGrantedAuthority simpleAuth = 
					new SimpleGrantedAuthority(auth.getAuthority()); // 문자열을 인자로 auth객체생성 
			authorities.add(simpleAuth);
		}
		loginUser.setAuthorities(authorities);
		
		//새로운 authentication객체 생성
		// 프린시팔, 크리덴셜, 어소리티
		Authentication newAuthentication = 
				new UsernamePasswordAuthenticationToken(
						loginUser,
					oldAuthentication.getCredentials(),
					oldAuthentication.getAuthorities()
				);
		//SecurityContextHolder - SecurityContext 하위에 설정
		SecurityContextHolder.getContext().setAuthentication(newAuthentication);
		
		//3. 사용자피드백 & 리다이렉트
		return "redirect:/security/myPage";
	}
	
	
	
	
    

    
	
	
}
