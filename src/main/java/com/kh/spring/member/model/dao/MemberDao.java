package com.kh.spring.member.model.dao;

import java.util.HashMap;

import com.kh.spring.member.model.vo.Member;

public interface MemberDao {

	Member loginUser(String userId);

	int insertMember(Member m);

	int updateMember(Member m);

	int idCheck(String userId);

	void updateMemberChagePwd();

	Member loginMember(Member m);

	HashMap<String, Object> selectOne(String userId);

}
