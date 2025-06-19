package com.kh.spring.board.model.dao;

import java.util.List;
import java.util.Map;

import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.board.model.vo.BoardImg;
import com.kh.spring.board.model.vo.BoardType;
import com.kh.spring.common.model.vo.PageInfo;

public interface BoardDao {

	int selectListCount(Map<String, Object> paramMap);

	List<Board> selectList(PageInfo pi, Map<String, Object> paramMap);

	List<BoardType> selectBoardTypeList();

	int insertBoard(Board b);

	int insertBoardImg(BoardImg bi);

	int insertBoardImgList(List<BoardImg> imgList);

	BoardExt selectBoard(int boardNo);

	int increaseCount(int boardNo);

	List<BoardImg> selectBoardImgList(int boardNo);

	int updateBoard(Board board);

	int deleteBoardImg(String deleteList);

	int updateBoardImg(BoardImg bi);

	List<String> selectFileList();

}
