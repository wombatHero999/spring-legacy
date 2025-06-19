package com.kh.spring.chat.model.service;

import java.util.List;

import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;

public interface ChatService {

	List<ChatRoom> selectChatRoomList();

	int openChatRoom(ChatRoom room);

	List<ChatMessage> joinChatRoom(ChatRoomJoin join);

	int insertMessage(ChatMessage chatMessage);

	void exitChatRoom(ChatRoomJoin join);

}
