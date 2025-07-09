package com.kh.spring.board.model.vo;

import java.util.List;

import lombok.Data;

// 상세보기 서비스용
@Data
public class BoardExt extends Board{	
	private List<BoardImg> imgList;
	private String userName;
}













