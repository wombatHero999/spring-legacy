package com.kh.spring.chat.model.dao;

import java.util.List;

import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;

public class ChatDao {
	

	public List<ChatRoom> selectChatRoomList() {
		return null;
	}

	public int openChatRoom(ChatRoom room) {
		return 0;
		
	}

	public int joinCheck(ChatRoomJoin join) {
		return 0;
	}

	public int joinChatRoom(ChatRoomJoin join) {
		return 0;
	}

	public List<ChatMessage> selctChatMessage(int chatRoomNo) {
		return null;
	}

	public int insertMessage(ChatMessage chatMessage) {
		return 0;
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
