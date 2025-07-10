package com.kh.spring.chat.model.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.kh.spring.chat.model.vo.ChatMessage;

@Controller
public class StompChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/sendMessage")
    public void sendMessage(@Payload ChatMessage message) {
        // 채팅 메시지 저장 또는 처리 로직 추가 가능
        messagingTemplate.convertAndSend("/topic/room/" + message.getChatRoomNo(), message);
    }

    @MessageMapping("/chat/enter")
    public void enter(@Payload ChatMessage message) {
        message.setType(ChatMessage.MessageType.ENTER);
        message.setMessage(message.getUserName() + "님이 입장하셨습니다.");
        messagingTemplate.convertAndSend("/topic/room/" + message.getChatRoomNo(), message);
    }

    @MessageMapping("/chat/leave")
    public void leave(@Payload ChatMessage message) {
        message.setType(ChatMessage.MessageType.LEAVE);
        message.setMessage(message.getUserName() + "님이 퇴장하셨습니다.");
        messagingTemplate.convertAndSend("/topic/room/" + message.getChatRoomNo(), message);
    }
}
