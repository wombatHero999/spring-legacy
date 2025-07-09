package com.kh.spring.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.board.model.vo.BoardImg;
import com.kh.spring.board.model.vo.BoardType;
import com.kh.spring.common.model.vo.PageInfo;

public interface BoardService {

	int selectListCount(Map<String, Object> paramMap);

	List<Board> selectList(PageInfo pi, Map<String, Object> paramMap);

	int insertBoard(Board b , List<BoardImg> imgList) ;

	BoardExt selectBoard(int boardNo);

	int increaseCount(int boardNo);

	List<BoardImg> selectBoardImgList(int boardNo);

	int updateBoard(Board board, List<BoardImg> imgList, String deleteList);

	List<String> selectFileList();

	List<BoardType> selectBoardTypeList();

	Map<String, String> getBoardTypeMap();

}
