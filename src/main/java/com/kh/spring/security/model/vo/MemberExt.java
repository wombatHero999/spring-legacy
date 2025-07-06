package com.kh.spring.security.model.vo;

import java.util.Collection;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kh.spring.member.model.vo.Member;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


// UserDetails
//  - 사용자의 인증 및 권한 정보를 제공하는데 사용하는 인터페이스로 스프링 시큐리티에서 제공
@Data @NoArgsConstructor	
@ToString(exclude = "userPwd")
public class MemberExt extends Member implements UserDetails{
	
	//AUTHORITY
	// SimpleGrantedAuthority
	//  - 문자열 형태의 권한("ROLE_USER", "ROLE_ADMIN")
	//  - authorities는 문자열 형태의 권한을 배열형태로 저장하는 변수
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
	
	// 스프링 시큐리티에서 비밀번호를로 사용하는 값을 반환하는 메소드
	// ID역할의 필드가 userName이 아니라면 오버라이딩 해줘야한다.
	@Override 
	public String getPassword() { 
		return getUserPwd();
	}
	
	// 스프링 시큐리티에서 로그인 아이디로 사용하는 값을 반환하는 메소드
	// ID역할의 필드가 userName이 아니라면 오버라이딩 해줘야한다.
	@Override
	public String getUsername() { 
		return getUserId();
	}

	// 실무에서는 계정 만료 상태나 잠금 여부를 DB에 컬럼으로 관리하고, 여기에 맞춰 반환한다.
	// 즉, 계정 만료상태 확인, 잠금상태 확인 로직이 필요.
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
