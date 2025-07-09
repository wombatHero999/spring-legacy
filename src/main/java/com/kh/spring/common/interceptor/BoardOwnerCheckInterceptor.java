package com.kh.spring.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.member.model.vo.Member;

public class BoardOwnerCheckInterceptor implements HandlerInterceptor {
	@Autowired
	private BoardService boardService; // 게시글 정보 조회를 위해 주입
	
	// 전처리(pre Handle) 함수 : 컨트롤러가 서블릿의 요청을 처리하기 "전"에 먼저 실행되는 함수
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 1. 현재 로그인한 사용자 정보 추출
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Member loginUser = (Member) authentication.getPrincipal();
		int loginUserNo = loginUser.getUserNo();

		// 2. 요청 URI에서 게시글 번호 추출 (예: /board/update/1001)
		String[] uriParts = request.getRequestURI().split("/");
		int boardNo = Integer.parseInt(uriParts[uriParts.length - 1]);

		// 3. 게시글 정보 조회
		BoardExt board = boardService.selectBoard(boardNo);

		if (board == null) {
			// 데이터가 존재하지 않는 경우.
	        response.sendRedirect(request.getContextPath() + "/security/accessDenied");
			return false;
		}

		// 4. 작성자 번호와 로그인한 사용자 번호 비교
		int boardWriterNo = Integer.parseInt(board.getBoardWriter());
		if (loginUserNo != boardWriterNo) {
			// 권한이 없는 경우 Security accessDenied 페이지로 리다이렉트
	        response.sendRedirect(request.getContextPath() + "/security/accessDenied");
			return false;
		}

		// 5. 통과
		return true;
	}

}
