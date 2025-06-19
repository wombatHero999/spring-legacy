package com.kh.spring.board.model.dao;


import java.util.List;
import java.util.Map;

import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.board.model.vo.BoardImg;
import com.kh.spring.board.model.vo.BoardType;
import com.kh.spring.common.model.vo.PageInfo;

public class BoardDaoImpl implements BoardDao{
	
	@Override
	public int selectListCount(Map<String, Object> paramMap) {
		return 0;
	}

	@Override
	public List<Board> selectList(PageInfo pi, Map<String, Object> paramMap) {
		return null;
	}

	@Override
	public List<BoardType> selectBoardTypeList() {
		return null;
	}

	@Override
	public int insertBoard(Board b) {
		return 0;
	}

	@Override
	public int insertBoardImg(BoardImg bi) {
		return 0;
	}

	@Override
	public int insertBoardImgList(List<BoardImg> imgList) {
		return 0;
	}

	@Override
	public BoardExt selectBoard(int boardNo) {
		return null;
	}

	@Override
	public int increaseCount(int boardNo) {
		return 0;
	}

	@Override
	public List<BoardImg> selectBoardImgList(int boardNo) {
		return null;
	}

	@Override
	public int updateBoard(Board board) {
		return 0;
	}

	@Override
	public int deleteBoardImg(String deleteList) {
		return 0;
	}

	@Override
	public int updateBoardImg(BoardImg bi) {
		return 0;
	}

	@Override
	public List<String> selectFileList() {
		return null;
	}

	
	
	
	
	
	
	
	
}
