package com.kh.spring.security.model.service;

import org.springframework.security.core.userdetails.UserDetailsService;

// UserDetailsService ? 스프링 시큐리티에서 인증처리를 위해 사용되는 클래스로 , 사용자 정보를 조회하는 메소드를 정의하고 있음
public interface SecurityService extends UserDetailsService {

}
