package com.kh.spring.chat.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;

@Repository
public class ChatDao {
	
	@Autowired
	private SqlSessionTemplate session;
	
	public List<ChatRoom> selectChatRoomList() {
		return session.selectList("chat.selectChatRoomList");
	}

	public int openChatRoom(ChatRoom room) {
		return session.insert("chat.openChatRoom", room);
		
	}

	public int joinCheck(ChatRoomJoin join) {
		return session.selectOne("chat.joinCheck" , join);
	}

	public int joinChatRoom(ChatRoomJoin join) {
		return session.insert("chat.joinChatRoom" , join);
	}

	public List<ChatMessage> selctChatMessage(int chatRoomNo) {
		return session.selectList("chat.selctChatMessage", chatRoomNo);
	}

	public int insertMessage(ChatMessage chatMessage) {
		return session.insert("chat.insertMessage",chatMessage);
	}

	public int exitChatRoom(ChatRoomJoin join) {
		return 0;
	}

	public int countChatRoomMember(ChatRoomJoin join) {
		return 0;
	}

	public int closeChatRoom(ChatRoomJoin join) {
		return 0;
	}

}
