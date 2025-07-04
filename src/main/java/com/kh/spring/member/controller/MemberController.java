package com.kh.spring.member.controller;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.vo.Member;

@Controller // Component-scan에 의해 bean객체 등록
@SessionAttributes({"loginUser"}) // Model에 들어가는 데이터 중 Session에 보관시킬 데이터를 설정하는 주석
public class MemberController {
		
	@Autowired
	private MemberService mService; //= new MemberServiceImpl();
	
	/*
	 * Spring의 DI(Dependency Injection)
	 *  - 의존성 주입
	 *  - 어플리케이션을 구성하는 객체를 개발자가 직접 생성하는게 아닌, 스프링이 생성한 객체를 주입받아서
	 *    사용하는 방식
	 *  - new연산자를 직접 사용하지 않고 , 자료형 선언만 한 후 @Autowired어노테이션을 통해 주입받음.
	 *  */
	
	@RequestMapping( value="/member/login" , method = RequestMethod.GET)
	public String loginMember() {
		return "member/login"; // forwarding할 jsp의 경로
	}
	
	// 스프링의 arugumentResolver
//	@RequestMapping( value="/member/login" , method = RequestMethod.POST)
//	public String login(HttpServletRequest request ) {
//		System.out.println(request.getParameter("userId"));
//		return "main";
//	}
	
	// @RequestParam어노테이션
	// servet의 request.getParameter("키")로 뽑은 역할을 대신 수행해주는 어노테이션
	// 클라이언트가 요청한 값을 대신 변환하여 바인딩 해주는 역할은 argumentResolver가 수행.
//	@RequestMapping( value="/member/login" , method = RequestMethod.POST)
//	public String login(@RequestParam(value="userId", defaultValue="mkm") String userId) {
//		System.out.println(userId);
//		return "main";
//	}
	
	// @RequestParam생략
	// 매개변수의 이름과 일치하는 rqeust의 파라미터값을 추출하여 바인딩
//	@RequestMapping( value="/member/login" , method = RequestMethod.POST)
//	public String login(String userId, String userPwd) {
//		System.out.println(userId+" :: "+ userPwd);
//		return "main";
//	}
	
	/* 
	 * 커맨드 객체 방식
	 *  메소드의 매개변수로 vo클래스 타입을 지정하는 경우, 요청 전달값의 name속성과 일치하는
	 *  VO클래스 필드의 Setter함수를 호출하여 바인딩
	 *  
	 *  매개변수의 클래스와 일치하는 클래스의 기본생성자 호출
	 *  파라미터의 key값과 클래스의 필드명이 일치하는 경우 setter함수 호출.
	 * */
//	@RequestMapping( value="/member/login" , method = RequestMethod.POST)
//	public String login(Member m) {
//		System.out.println(m.getUserId()+" :: "+ m.getUserPwd());
//		return "main";
//	}
	
//	@RequestMapping( value="/member/login" , method = RequestMethod.POST)
//	public ModelAndView login(Member m , ModelAndView mv , Model model) {
//		/*
//		 * 로그인 비지니스 작업 처리 완료 후, "응답 데이터"를 담아 응답 페이지로 redirect(재요청) 
//		 * 
//		 * 응답데이터를 담을 수 있는 객체
//		 * 1) Model 
//		 *  - 포워딩할 응답 뷰페이지로 전달하고자 하는 데이터를 맵형식으로 담을 수 있는 객체
//		 *  - 기본 requestScope에, 설정을 통해 session Scope에도 데이터를 담을 수 있다.
//		 *  - 클래스 선언부에 @SessionAttributes를 추가하면 데이터가 session Scope로 저장
//		 * 
//		 * 2) ModelAndView
//		 *  - ModelAndView에서 Model은 데이터를 담을 수 있는 맵형태의 객체
//		 *  - View는 이동할 페이지에 대한 정보를 담고 있는 객체
//		 *  - 기본 requestScope에 데이터를 보관
//		 *   
//		 * model은 내부에 데이터를추가시 addAttribute()함수를 사용하여 데이터를 추가
//		 * modelAndView는 데이터 추가시 addObject()를 사용하며 view설정시 setViewName()를 사용
//		 *  */
//		
//		model.addAttribute("errorMsg","오류발생");
//		
//		mv.addObject("errorMsg","오류발생");
//		mv.setViewName("common/errorPage");
//		
//		return mv;
//	}
	
	//@RequestMapping( value="/member/login" , method = RequestMethod.POST)
	@PostMapping("/member/login")
	public ModelAndView login(
			@ModelAttribute Member m , 
			ModelAndView mv , 
			Model model , 
			HttpSession session, // 로그인 성공시 , 사용자 정보를 보관할 객체
			RedirectAttributes ra
			) {
		// 로그인 요청 처리
		Member loginUser = mService.loginMember(m);
		// 로그인 성공 시 회원정보, 실패시 null값이 전달
		
		if(loginUser != null) {
			// 사용자 인증 정보(loginUser)를 session에 보관
			//session.setAttribute("loginUser", loginUser);
			model.addAttribute("loginUser",loginUser);
			
			//session.setAttribute("alertMsg", "로그인 성공!");
			ra.addFlashAttribute("alertMsg" , "로그인 성공!!");
			/* 
			 * RedirectAttributes의 flashAttribute는 데이터를 sessionScope에 담고,
			 * 리다이렉트가 완료되면, sessionScope에 있던 데이터를 requestScope로 변경
			 * */
		}else {
			//session.setAttribute("alertMsg", "로그인 실패!");
			ra.addFlashAttribute("alertMsg","로그인 실패!");
		}
		
		mv.setViewName("redirect:/");// 메인페이지로 리다이렉트
		
		return mv;
	}
	
	@GetMapping("/member/logout")
	public String logout(HttpSession session, SessionStatus status) {
		// 로그아웃 방법
		// 1. session내부의 인증정보를 무효화
		//session.invalidate(); // 세션 내부의 모든 데이터를 삭제
		status.setComplete(); // model로 session scope에 이관된 데이터를 비우는 메서드
		
		return "redirect:/";
	}
	
	@GetMapping("/member/insert")
	public String enrollForm() {
		return "member/memberEnrollForm";
	}
	
	@PostMapping("/member/insert")
	public String insertMember(Member m,
			Model model,
			RedirectAttributes ra
			) {
		int result = mService.insertMember(m);
		
		String viewName = "";
		
		if(result > 0) {
			ra.addFlashAttribute("alertMsg","회원가입 성공 ^^");
			viewName = "redirect:/";
		}else {
			model.addAttribute("errorMsg","회원가입 실패 ㅠㅠ");
			viewName = "common/errorPage";
		}
		
		return viewName;
	}
	
	@GetMapping("/member/myPage")
	public String myPage() {
		return "member/myPage";
	}
	
	@PostMapping("/member/update")
	public String updateMember(
			Member m ,
			RedirectAttributes ra, 
			Model model
			) {
		int result = mService.updateMember(m);
		String url = "";
		
		// throw new RuntimeException("예외발생");
		
		if(result > 0) {
			// 업데이트 성공시 
			ra.addFlashAttribute("alertMsg","내 정보 수정 성공");
			url = "redirect:/member/logout";
		}else {
			model.addAttribute("errorMsg","회원정보 수정 실패");
			url = "common/errorPage";
		}
		
		return url;
	}
	
	/* 
	 * 스프링 예외처리 방법.
	 * 1. try - catch로 메서드별 예외처리 --> 1순위로 적용
	 * 2. 하나의 컨트롤러에서 발생하는 예외들을 모아서 처리하는 방법 --> 2순위로 적용
	 *   컨트롤러에 예외처리 메서드를 1개 추가한 후, @ExceptionHandler 추가
	 * 3. 어플리케이션 전역에서 발생하는 예외를 모아서 처리하는 방법 --> 3순위
	 *   새로운 클래스 작성 후 ,클래스에 @ControllerAdvice를 추가
	 * */
	
//	@ExceptionHandler
//	public String exceptionHandelr(Exception e, Model model) {
//		e.printStackTrace();
//		
//		model.addAttribute("errorMsg","서비스 이용 중 문제가 발생했습니다.");
//		return "common/errorPage";
//	}
	
	// 비동기 요청
	@ResponseBody // 반환되는 값이 값 그자체임을 의미하는 주석
	@GetMapping("/member/idCheck")
	public String idCheck(String userId) {
		int result = mService.idCheck(userId); // 아이디 존재시 1, 없다면 0
		
		// 컨트롤러에서 반환되는 값은 기본적으로 forward경로 혹은 redirect를 위한 경로로 해석한다.
		// 즉, 반환되는 문자열 값은 "특정 경로"로써 인식을 하는데
		// 경로가 아닌 값 자체를 반환시키기 위해서는 @ResponseBody이 필요하다.
		
		return result+""; // 1 , 0 -> /WEB-INF/views/0.jsp 
	}
	
	
//	@PostMapping("/member/selectOne")
//	@ResponseBody
//	public HashMap<String, Object> selectOne(String userId){
//		HashMap<String,Object> map = mService.selectOne(userId);
//		
//		// jackson-databind를 활용하여, VO클래스, ArrayList,map데이터를 자동으로 JSON형태로 바인딩하기.
//		return map;
//	}
	
	@PostMapping("/member/selectOne")
	public ResponseEntity<HashMap<String, Object>> selectOne(String userId){
		HashMap<String,Object> map = mService.selectOne(userId);
		ResponseEntity res = null;
		if(map != null) {
			res = ResponseEntity
					.ok() // 응답상태 200
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
					.body(map);					
		}else {
			res = ResponseEntity
					.notFound() //응답상태 404
					.build();
		}
		return res;
	}
	
	
	
	
	
	
	
	
	
	
	
}







