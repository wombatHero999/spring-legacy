package com.kh.spring.common.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.kh.spring.member.model.vo.Member;

public class CustomHandshakeInterceptor implements HandshakeInterceptor {

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
			WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		
		HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
		HttpSession httpSession = servletRequest.getSession(false);

		if (httpSession != null) {
			SecurityContext securityContext = (SecurityContext) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
			if (securityContext != null) {
				Authentication auth = securityContext.getAuthentication();
				if (auth != null && auth.getPrincipal() instanceof Member) {
					Member loginUser = (Member) auth.getPrincipal();
					attributes.put("userNo", loginUser.getUserNo());
					attributes.put("userName", loginUser.getUserName());
				}
			}
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		
	}
}
