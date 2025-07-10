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
	private SqlSessionTemplate sqlSession;

	public List<ChatRoom> selectChatRoomList() {
		return sqlSession.selectList("chatMapper.selectChatRoomList");
	}

	public int openChatRoom(ChatRoom room) {
		
		int result = sqlSession.insert("chatMapper.openChatRoom",room);
		
		if(result > 0) {
			result = room.getChatRoomNo();//추가된 pk값
		}
		
		return result;
		
	}

	public int joinCheck(ChatRoomJoin join) {
		return sqlSession.selectOne("chatMapper.joinCheck", join);
	}

	public int joinChatRoom(ChatRoomJoin join) {
		return sqlSession.insert("chatMapper.joinChatRoom" , join);
	}

	public List<ChatMessage> selctChatMessage(int chatRoomNo) {
		return sqlSession.selectList("chatMapper.selectChatMessage", chatRoomNo);
	}

	public int insertMessage(ChatMessage chatMessage) {
		return sqlSession.insert("chatMapper.insertMessage", chatMessage);
	}

	public int exitChatRoom(ChatRoomJoin join) {
		return sqlSession.delete("chatMapper.exitChatRoom", join);
	}

	public int countChatRoomMember(ChatRoomJoin join) {
		return sqlSession.selectOne("chatMapper.countChatRoomMember" , join);
	}

	public int closeChatRoom(ChatRoomJoin join) {
		return sqlSession.update("chatMapper.closeChatRoom", join);
	}

}
