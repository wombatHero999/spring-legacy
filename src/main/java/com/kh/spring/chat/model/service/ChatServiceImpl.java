package com.kh.spring.chat.model.service;

import java.util.List;


import com.kh.spring.chat.model.dao.ChatDao;
import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;

public class ChatServiceImpl implements ChatService{
	
	private ChatDao dao;

	@Override
	public List<ChatRoom> selectChatRoomList() {
		return null;
	}

	@Override
	public int openChatRoom(ChatRoom room) {
		return 0;
	}

	@Override
	public List<ChatMessage> joinChatRoom(ChatRoomJoin join) {
		return null;
	}

	@Override
	public int insertMessage(ChatMessage chatMessage) {
		return 0;
	}

	@Override
	public void exitChatRoom(ChatRoomJoin join) {
		
	}
	
	
	
	
	
	

}
