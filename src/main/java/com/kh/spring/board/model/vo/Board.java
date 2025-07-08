package com.kh.spring.board.model.vo;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Board {
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private String boardCd;
	private String boardWriter; // userNo, userName
	private int count;
	private Date createDate;
	private String status;
	
}



