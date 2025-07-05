package com.kh.spring.security.model.vo;

import java.util.Collection;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kh.spring.member.model.vo.Member;

import lombok.Data;
import lombok.NoArgsConstructor;


// UserDetails
//  - 사용자의 인증 및 권한 정보를 제공하는데 사용하는 인터페이스로 스프링 시큐리티에서 제공
@Data @NoArgsConstructor	
public class MemberExt extends Member implements UserDetails{
	

	// SimpleGrantedAuthority
	//  - 문자열 형태의 권한("ROLE_USER", "ROLE_ADMIN")
	//  - authorities는 문자열 형태의 권한을 배열형태로 저장하는 변수
	private List<SimpleGrantedAuthority> authorities; // authorities
	// private String userName; // SpringSecurity에서 사용자 ID를 의미하는 변수로 사용됨.
	private boolean enabled; // 활성화 여부 1 == true , 0 == false , 활성화 여부가 true여야 로그인이 가능함.
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
	//로그인한 사용자의 비밀번호를 반환해주는 메소드
	@Override 
	public String getPassword() { // SpringSecurity에서 사용자의 비밀번호를 반환하는 메서드.
		return getUserPwd();
	}
	//로그인한 사용자의 이름을 반환해주는 메소드
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
