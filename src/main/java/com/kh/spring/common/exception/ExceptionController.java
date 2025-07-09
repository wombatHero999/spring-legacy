package com.kh.spring.common.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// 어플리케이션 전역의 컨트롤러에 공통으로 추가할 예외코드를 작성할 클래스
@ControllerAdvice
public class ExceptionController {
	@ExceptionHandler
	public String exceptionHandler(Exception e, Model model) {
		e.printStackTrace();
		model.addAttribute("errorMsg", "서비스 이용 중 문제가 발생했습니다");
		
		return "common/errorPage";
	}
}













