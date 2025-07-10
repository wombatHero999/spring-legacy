package com.kh.spring.chat.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.chat.model.dao.ChatDao;
import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;
import com.kh.spring.common.Utils;

@Service
public class ChatServiceImpl implements ChatService{
	
	@Autowired
	private ChatDao dao;
	
	@Override
	public List<ChatRoom> selectChatRoomList() {
		return dao.selectChatRoomList();
	}

	@Override
	public int openChatRoom(ChatRoom room) {
		return dao.openChatRoom(room);
	}

	@Override
	public List<ChatMessage> joinChatRoom(ChatRoomJoin join) {
		//CHAT_ROOM_JOIN에 데이터 INSERT 후,
		//채팅메세지 목록 조회후 반환
		List<ChatMessage> list = null;
		
		// 1. 현재 회원이 해당 채팅방에 참여하고 있는지 확인.(select)
		int result = dao.joinCheck(join);
		// 2. 참여하고 있지 않다면 참여(insert)
		if(result == 0) {
			result = dao.joinChatRoom(join);
		}
		
		//채팅메세지 조회후 반환
		if(result > 0) {
			list =  dao.selctChatMessage(join.getChatRoomNo());
		}
		
		return list;
	}

	@Override
	public int insertMessage(ChatMessage chatMessage) {
		
		chatMessage.setMessage(Utils.XSSHandling(chatMessage.getMessage()));
		chatMessage.setMessage(Utils.newLineHandling(chatMessage.getMessage()));
		return dao.insertMessage(chatMessage);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void exitChatRoom(ChatRoomJoin join) {
		
		// 1) 채팅방 나가기 -> CHAT_ROOM_JOIN테이블에서 DELETE문 수행
		int result = dao.exitChatRoom(join);
		
		// 2) 현재 채팅방 인원이 0명인 경우 -> CHAT_ROOM테이블에서 STATUS값 업데이트
		if(result > 0) {
			
			// 현재 채팅방 인원이 몇 명인지 확인
			int cnt = dao.countChatRoomMember(join);
			
			// 내가 마지막으로 나간 경우 -> Chat_ROMM테이블에서 STATUS상태값을 변경 UPDATE
			if(cnt == 0) {
				result = dao.closeChatRoom(join);
			}
			
		}
	}
	
	
	
	
	
	
	

}
