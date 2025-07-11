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

    @MessageMapping("/chat/message")  // 클라이언트에서 /pub/chat/message로 전송
    public void sendMessage(@Payload ChatMessage chatMessage) {
        if ("ENTER".equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getUserName() + "님이 입장하셨습니다.");
        } else if ("QUIT".equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getUserName() + "님이 퇴장하셨습니다.");
        }

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getChatRoomNo(), chatMessage);
    }
    
    @MessageMapping("/chat/sendMessage")
    public void sendMessage2(@Payload ChatMessage message) {
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
