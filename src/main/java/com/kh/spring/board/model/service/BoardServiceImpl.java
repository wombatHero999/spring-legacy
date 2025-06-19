package com.kh.spring.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.dao.BoardDao;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.board.model.vo.BoardImg;
import com.kh.spring.board.model.vo.BoardType;
import com.kh.spring.common.model.vo.PageInfo;


public class BoardServiceImpl implements BoardService {

	private BoardDao boardDao;

	@Override
	public int selectListCount(Map<String, Object> paramMap) {
		return 0;
	}

	@Override
	public List<Board> selectList(PageInfo pi, Map<String, Object> paramMap) {
		return null;
	}

	@Override
	public int insertBoard(Board b, List<BoardImg> imgList) throws Exception {

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
	public int updateBoard(Board board, String deleteList, MultipartFile upfile, List<MultipartFile> upfiles)
			throws Exception {

		return 0;
	}

	@Override
	public List<String> selectFileList() {

		return null;
	}

	@Override
	public List<BoardType> selectBoardTypeList() {

		return null;
	}

}
