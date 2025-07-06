package com.kh.spring.security.model.service;

import org.springframework.security.core.userdetails.UserDetailsService;

/*
 * UserDetailsService
    - 스프링 시큐리티에서 인증 처리 시 사용되는 핵심 인터페이스로, 
      사용자 정보를 조회하는 메서드(loadUserByUsername())를 정의하고 있다.
   loadUserByUsername() 
    - 사용자 이름(username)을 기반으로 UserDetails 객체를 반환하며, 
      반환된 UserDetails는 비밀번호 및 권한 정보 검증에 사용됨.
    - 보통 DB나 외부 시스템에서 사용자 정보를 조회하는 구현체를 작성하여 사용함.
*/
public interface SecurityService extends UserDetailsService {

}
