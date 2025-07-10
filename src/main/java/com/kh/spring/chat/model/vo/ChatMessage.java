package com.kh.spring.chat.model.vo;

import lombok.Data;

@Data
public class ChatMessage {
	
	private int cmNo;
	private String message;
	private String createDate;
	private int chatRoomNo;
	private int userNo;
	
	private String userName;
	
	public enum MessageType {
        ENTER, TALK, LEAVE
    }

    private MessageType type;
    
}
