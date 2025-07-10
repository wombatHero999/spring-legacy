package com.kh.spring.chat.model.websocket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.spring.chat.model.service.ChatService;
import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatWebsocket extends TextWebSocketHandler{
	/*
	 * TextWebSocketHandler
	 *  - 텍스트 기반의 메세지처리를 위한 메소드를 제공하는 웹소켓 클래스
	 *  - 웹소켓 핸들러를 기반으로 같은 채팅방에 접속한 사용자들간에 동일한 메시지를 주고받을 수 있도록 설계
	 *  */
	
	@Autowired
	private ChatService chatService;
	
	// Jackson-databind의 objectMapper
	//  - JSON -> VO, VO -> JSON로 파싱하기 위해 선언
	
	private final ObjectMapper objectMapper = new ObjectMapper();

	// 채팅방별 WebSocketSession을 관리하기 위한 필드
	private final Map<Integer, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
	/*
	 * WebSocketSession
	 *  - 클라이언트가 웹소켓을 통해 연결하고 있는 동안 유지되는 세션
	 *  - 사용자가 페이지를 새로고침하거나 브라우저를 닫으면 세션은 끊기고 새로운 세션이 추가된다.
	 *  - 따라서 session.getId()값은 접속시 마다 달라질 수 있으며 , 고정된 사용자 식별자를 별도로
	 *    저장해두는 것이 필요하다.
	 * synchronizedSet
	 *  - 동기화된 set를 반환해주는 메소드
	 *  - 멀티스레드환경에서 하나의 컬렉션요소에 여러 스레드가 동시에 접근하게 되면 충돌이 발생할 수 있으므로 동기화 처리를 진행함.
	 *  */
	/* 클라이언트와 웹소켓 연결이 완료된 이후 통신할 준비가 되면 실행되는 함수 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		int chatRoomNo = (int) session.getAttributes().get("chatRoomNo");
		log.debug("웹소켓 연결됨. 세션 ID: {}, 채팅방 번호: {}", session.getId(), chatRoomNo);
	    // 해당 채팅방의 세션 Set이 없으면 생성
	    roomSessions.computeIfAbsent(chatRoomNo, k -> Collections.synchronizedSet(new HashSet<>()));
	    // 채팅방에 세션 추가
	    roomSessions.get(chatRoomNo).add(session);
	}
	
	/* 클라이언트와 웹소켓 연결이 종료되면 실행되는 함수 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// 웹소켓 연결이 종료되는 경우 sessions내부에 있는 클라이언트의 session정보를 삭제
		int chatRoomNo = (int) session.getAttributes().get("chatRoomNo");
		log.info("웹소켓 연결 종료. 세션 ID: {}, 채팅방 번호: {}", session.getId(), chatRoomNo);
	    Set<WebSocketSession> roomSet = roomSessions.get(chatRoomNo);
	    if (roomSet != null) {
	        roomSet.remove(session);
	        
	        // 방에 더 이상 사용자가 없으면 map에서 제거 (선택사항)
	        if (roomSet.isEmpty()) {
	            roomSessions.remove(chatRoomNo);
	        }
	    }
	}
	
	/* 클라이언트로부터 메세지(message)가 도착했을시 실행되는 함수 */
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		//TextMessage : 웹소켓을 이용해 전달된 데이터(텍스트)가 담겨있는 객체.
		// payload : 전송되는 데이터 담겨있는 필드(JSON객체로 전달받음)
		log.debug("메시지 {}", message); // paylod, byteCount, last 끝
		log.debug("메시지 body {}" , message.getPayload());
		
		// JSON -> VO
		ChatMessage chatMessage = 
				objectMapper.readValue(message.getPayload(), ChatMessage.class);
		// 전달받은 메세지를 db에 CHAT_MESSAGE테이블에 추가
		int result = chatService.insertMessage(chatMessage);
		
		// 메시지 브로드 캐스트
		if(result > 0) {
			// 같은 채팅방을 이용중인 사용자들에게 전달 메시지 전송
			Set<WebSocketSession> roomSet = roomSessions.get(chatMessage.getChatRoomNo());
			if (roomSet != null) {
				for(WebSocketSession s: roomSet) {
					try {
						String json = objectMapper.writeValueAsString(chatMessage);
			            s.sendMessage(new TextMessage(json));
					}catch(Exception e) {
						log.error("메시지 전송 실패", e);
					}
				}
			}
		}
	}
	
}
