package com.kh.spring.board.model.vo;

import java.util.List;

import com.kh.spring.member.model.vo.Member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class BoardExt extends Board{		
	private List<BoardImg> imgList;	
	//댓글목록
	//유저정보
	//private Member user;
	private String userName;
}
