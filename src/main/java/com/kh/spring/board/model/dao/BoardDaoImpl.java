package com.kh.spring.board.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.board.model.vo.BoardImg;
import com.kh.spring.board.model.vo.BoardType;
import com.kh.spring.common.model.vo.PageInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class BoardDaoImpl implements BoardDao{
	
	@Autowired
	private SqlSession sqlSession;


	@Override
	public Map<String, String> getBoardTypeMap() {
		// selectMap
		//  - Map<key, value>형태의 데이터로 반환시켜주는 메서드
		//  - 두번째 매개변수로 어떤 칼럼을 key로 사용할지 문자열형태로 작성
		//  - SELECT board_cn board_code , board_name FROM BOARD_TYPE 작성시 
		//    board_code를 키값으로 board_name이 바인딩 된다.
		return sqlSession.selectMap("boardMapper.selectBoardTypeMap","boardCd");
	}

	
	@Override
	public int selectListCount(Map<String, Object> paramMap) {
		return sqlSession.selectOne("boardMapper.selectListCount", paramMap);
	}

	@Override
	public List<Board> selectList(PageInfo pi, Map<String, Object> paramMap) {
		
		int offset = (pi.getCurrentPage() -1 ) * pi.getBoardLimit();
		int limit = pi.getBoardLimit();
		
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return sqlSession.selectList("boardMapper.selectList" , paramMap ,rowBounds);
	}

	@Override
	public List<BoardType> selectBoardTypeList() {
		return sqlSession.selectList("boardMapper.selectBoardTypeList");
	}

	@Override
	public int insertBoard(Board b) {
		log.info("등록 이전 b {}" , b);
		int result = sqlSession.insert("boardMapper.insertBoard" , b);
		log.info("등록 이후 b {}" , b);
		return result;
	}

	@Override
	public int insertBoardImg(BoardImg bi) {
		return sqlSession.insert("boardMapper.insertBoardImg", bi);
	}

	@Override
	public int insertBoardImgList(List<BoardImg> list) {
		return sqlSession.insert("boardMapper.insertBoardImgList", list);
	}

	@Override
	public BoardExt selectBoard(int boardNo) {
		//버전1) 쿼리문 한번실행
		return sqlSession.selectOne("boardMapper.selectBoard", boardNo);
		
		//버전2) 쿼리문 두번실행
		//return sqlSession.selectOne("boardMapper.selectBoardOnly",boardNo);
	}

	@Override
	public int increaseCount(int boardNo) {
		return sqlSession.update("boardMapper.increaseCount", boardNo);
	}

	@Override
	public List<BoardImg> selectBoardImgList(int boardNo) {
		return sqlSession.selectList("boardMapper.selectBoardImgList", boardNo);
	}

	@Override
	public int updateBoard(Board board) {
		return sqlSession.update("boardMapper.updateBoard", board);
	}

	@Override
	public int deleteBoardImg(String deleteList) {
		return sqlSession.delete("boardMapper.deleteBoardImg", deleteList);
	}

	@Override
	public int updateBoardImg(BoardImg bi) {
		return sqlSession.update("boardMapper.updateBoardImg", bi);
	}

	@Override
	public List<String> selectFileList() {
		return sqlSession.selectList("boardMapper.selectFileList");
	}

	
	
	
	
	
	
	
	
	
	
}
