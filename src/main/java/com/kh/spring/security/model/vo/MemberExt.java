package com.kh.spring.security.model.vo;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kh.spring.member.model.vo.Member;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor	
public class MemberExt extends Member implements UserDetails{
	
	// 복수개의 권한을 관리
	// 문자열data("ROLE_USER", "ROLE_ADMIN")를 처리할 수 있는 GrantedAuthority의 하위클래스
	private List<SimpleGrantedAuthority> authorities; // authorities
	
	/**
	 * Collection - List/Set
	 * 
	 * Collection<? extends GrantedAuthority> 
	 * 	- <GrantedAuthority를 상속하는 ?> -> 자식타입(상한선)
	 *  - <? super Integer> -> Integer 부모타입 (하한선)
	 * Collection<GrantedAuthority>
	 * 
	 * 사용자가 가지고 있는 권한 목록을 반환할 getter메서드
	 */	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override 
	public String getPassword() { // SpringSecurity에서 사용자의 비밀번호를 반환하는 메서드.
		return getUserPwd();
	}
	
	@Override
	public String getUsername() { // SpringSecurity에서 사용자의 ID를 반환하는 메서드.
		return getUserId();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
