package com.kh.spring.board.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BoardType {
	private String boardCd;
	private String boardName;// 일반게시판, 사진게시판, 음악, 영화, 게임
}
