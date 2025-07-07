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

@Data
@NoArgsConstructor
public class MemberExt extends Member implements UserDetails {
	
	// SimpleGrantedAuthority
	//  - 문자열 형태의 권한("ROLE_USER", "ROLE_ADMIN")
	//  - ROLE_권한 작성
	private List<SimpleGrantedAuthority> authorities;
	
	// 사용자가 가진 권한 목록을 반환하는 메서드.
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	/*
	 * 	getPassword / getUsername
	 *  - 스프링 시큐리티에서 비밀번호/아이디를 가져올 때 사용하는 메서드.
	 *  - id역할의 필드와 비밀번호 역할의 필드가 username,password가 아니라면 오버라이딩 해줘야 함.
	 *  */
	
	@Override
	public String getPassword() {
		return getUserPwd();
	}

	@Override
	public String getUsername() {
		return getUserId();
	}
	
	/*
	 * 계정 만료상태, 잠금상태 등을 확인하는 메서드.
	 *  */
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








