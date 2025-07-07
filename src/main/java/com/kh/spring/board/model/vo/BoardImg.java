package com.kh.spring.board.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BoardImg {
	private int boardImgNo;
	private String originName;
	private String changeName;
	private int refBno;
	private int imgLevel;
}
