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
     * 사용자 인증 프로세스(UserDetailsService의 loadUserByUsername내용을 재정의.)
	 * 1) 사용자가 제공한 인증 정보를 검증하여 사용자실제 존재하는 회원인지 확인.
	 * 존재한다면 사용자의 아이디, 권한을 가지고있는 	 
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
