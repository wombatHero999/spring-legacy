package com.kh.spring.chat.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatRoom {
	
	private int chatRoomNo;
	private String title;
	private String status;
	private int userNo;
	
	private String userName;
	private int cnt;
}
