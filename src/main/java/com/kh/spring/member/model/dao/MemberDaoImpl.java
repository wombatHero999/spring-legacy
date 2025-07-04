package com.kh.spring.member.model.dao;

import java.util.HashMap;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.member.model.vo.Member;

@Repository
public class MemberDaoImpl implements MemberDao{
	
	@Autowired
	private SqlSessionTemplate session;
	
	@Override
	public Member loginMember(Member m) {
		return session.selectOne("member.loginMember", m);
	}
	
	@Override
	public Member loginUser(String userId) {
		
		return null;
	}

	@Override
	public int insertMember(Member m) {
		return session.insert("member.insertMember", m);
	}

	@Override
	public int updateMember(Member m) {
		return session.update("member.updateMember", m);
	}

	@Override
	public int idCheck(String userId) {
		return session.selectOne("member.idCheck", userId);
	}

	@Override
	public void updateMemberChagePwd() {
		
	}

	@Override
	public HashMap<String, Object> selectOne(String userId) {
		return session.selectOne("member.selectOne" , userId);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
