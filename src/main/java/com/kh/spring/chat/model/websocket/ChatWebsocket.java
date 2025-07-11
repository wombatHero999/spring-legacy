package com.kh.spring.chat.model.websocket;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.spring.chat.model.service.ChatService;
import com.kh.spring.chat.model.vo.ChatMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatWebsocket extends TextWebSocketHandler{
	// TextWebSocketHandler -> 텍스트 기반의 메세지 처리를 위한 메소드를 제공하는 웹소켓 클래스
	
	@Autowired
	private ChatService chatService;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	// 채팅방별 WebSocketSession을 관리
	private final Map<Integer, Set<WebSocketSession>> roomSession = new HashMap<>();
	/* 
	 * WebSocketSession
	 *  - 클라이언트가 웹소켓을 통해 연결하고 있는 동안 유지되는 세션
	 *  - 사용자가 페이지를 새로고침하거나 브라우저를 닫으면 세션은 끊기고 새로운 세션이 추가된다.
	 * */
	
	// 클라이언트와 서버간에 웹소켓 연결이 완료된 이후에 실행되는 함수.
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		/* 
		 * session안에 담겨 있는 채팅방 번호를 꺼내고, roomSessions에서 채팅방 번호에 맞는 set객체를 반환
		 * 만약 채팅방에 Set객체가 없으면 새롭게 생성후 데이터를 추가.
		 * */
		int chatRoomNo = (int) session.getAttributes().get("chatRoomNo");
		log.debug("웹소켓 연결 완료. 세션 ID :{} , 채팅방 번호 : {}", session.getId(), chatRoomNo);
		Set<WebSocketSession> sessions = roomSession.get(chatRoomNo);
		if(sessions == null) {
			sessions = Collections.synchronizedSet(new HashSet<>());
			roomSession.put(chatRoomNo, sessions);	
		}
		sessions.add(session);
	}
	
	// 웹소켓 연결 종료 후 클라이언트의 session정보 삭제
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		int chatRoomNo = (int) session.getAttributes().get("chatRoomNo");
		log.debug("웹소켓 연결 종료. 세션 id {} , 채팅방 번호 : {}",session.getId(),chatRoomNo);
		Set<WebSocketSession> roomSet = roomSession.get(chatRoomNo);
		if(roomSet != null) {
			roomSet.remove(session);
			
			// 모든 사용자가 방에 없으면 map에서 제거시키기
			if(roomSet.isEmpty()) {
				roomSession.remove(chatRoomNo);
			}
		}
	}
	
	// 클라이언트가 메시지를 전달하는 경우 실행되는 함수
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// TextMessage : 웹소켓을 이용해 전달된 데이터가 담긴 객체
		log.debug("메시지 {} ", message);
		log.debug("메시지 body {}", message.getPayload());
		
		// JSON -> VO
		ChatMessage chatMessage = 
				objectMapper.readValue(message.getPayload(), ChatMessage.class );
		
		// 전달받은 메시지를 chat_message테이블에 추가
		int result = chatService.insertMessage(chatMessage);
		String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		chatMessage.setCreateDate(current);
		// 메시지 브로드캐스트
		if(result > 0) {
			// 같은 채팅방을 이용중인 사용자들에게 미시지 전송
			Set<WebSocketSession> roomSet = roomSession.get(chatMessage.getChatRoomNo());
			if(roomSet != null) {
				for(WebSocketSession s  : roomSet) {
					String json = objectMapper.writeValueAsString(chatMessage); // VO -> JSON
					s.sendMessage(new TextMessage(json)); // 클라이언트에게 메시지를 전달하는 함수
				}
			}
		}
		
	}	
	
}












