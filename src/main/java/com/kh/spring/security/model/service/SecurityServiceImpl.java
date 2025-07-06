package com.kh.spring.security.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kh.spring.security.model.dao.SecurityDao;

import lombok.extern.slf4j.Slf4j;

@Service("securityService")
@Slf4j
public class SecurityServiceImpl implements SecurityService {

	@Autowired
	private SecurityDao securityDao;
	

	/* 
	 * 사용자 인증 프로세스
	 * 1) 사용자가 로그인 시 입력한 아이디를 기반으로 사용자 정보를 조회하는 메서드.
	 * 2) 조회된 사용자 정보(UserDetails)는 스프링 시큐리티가 비밀번호 및 권한 검증에 사용함.
	 * 3) 해당 아이디로 사용자를 찾지 못하면 UsernameNotFoundException을 던져 인증 실패 처리됨.
	 * 4) 인증 성공 시 UserDetails 객체를 반환.
	 */
	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		UserDetails member = securityDao.loadUserByUsername(id);
		log.debug("member = {}", member);
		if(member == null)
			throw new UsernameNotFoundException(id);
		return member;
	}

}
