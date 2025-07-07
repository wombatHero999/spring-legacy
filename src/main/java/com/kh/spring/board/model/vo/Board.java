package com.kh.spring.board.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor // 기본생성자
@AllArgsConstructor // 매개변수 모두있는 생성자
@Data // setter, getter, equals, tostring
//to @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode
public class Board {
	private int boardNo; 
	private String boardTitle;
	private String boardContent;
	private String boardWriter; // id, userNo, name 
	private int count;
	private Date createDate;
	private String status;
	private String originName;
	private String changeName;
	private String boardCd;
}
