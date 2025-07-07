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
		/*
		 * 특정 페이지의 데이터를 가져오는 방법(페이징 처리)
		 * 
		 * 1. ROWNUM, ROW_NUMBER()로 페이징 처리된 쿼리 조회하기(인라인뷰 2중첩 필요)
           SELECT * ROWNUM기준 특정 ROW조회
		   FROM (
			    SELECT ROWNUM AS RNUM, INNER.* 정렬결과에 대한 ROWNUM 조회
			    FROM (
			        -- 특정 칼럼을 기준으로 정렬된 쿼리
			    ) INNER
			)
			WHERE RNUM BETWEEN #{startRow} AND #{endRow}
			- 쿼리문이 복잡하고, 코드의 가독성이 떨어지나 필요한 행만 조회하여 가져올 수 있기 때문에 메모리 낭비나 성능저하가 발생하지 앟는다.
			- 1페이지 -> WHERE RNUM BETWEEN 1 AND 10
			- 2페이지 -> WHERE RNUM BETWEEN 11 AND 20 
			- 3페이지 -> WHERE RNUM BETWEEN 21 AND 30
		 * 
		 * 2. RowBounds
		 *  - MyBatis에서 쿼리 결과에 대해 간단하게 페이징 처리(offset/limit)를 적용하는 도구
		 *    전체 쿼리결과를 메모리로 가져온 후 잘라내는 방식으로 로드한 데이터가 수만건 이상인경우 메모리낭비와 성능저하가 발생한다. 
		 *  - "소규모 데이터 쿼리"시 사용하는 것을 권장 
		 *      - offset : 몇 번째 행부터 가져올지 (시작 위치)
         		- limit  : 몇 개 행을 가져올지 (가져올 행 수)
           
           3. OFFSET FETCH를 사용하여 간단하게 쿼리 조회(ORACLE 12c이상인경우)
            - 코드의 복잡성을 줄이고 가독성을 크게 확보하는 방식으로 성능적으로 2번과 동일하다.
            [표현법]
            SELECT 
			    ... 조회할 칼럼
			FROM 테이블
			  ...
			ORDER BY 정렬조건 DESC
			OFFSET #{offset} ROWS FETCH NEXT #{limit} ROWS ONLY
			
			 - offset번째 행에서부터 limit갯수를 가져온다(fetch)
			 - 1페이지 -> OFFSET 1 ROWS FETCH NEXT 10 ROWS ONLY
			 - 2페이지 -> OFFSET 11 ROWS FETCH NEXT 10 ROWS ONLY
			 - 3페이지 -> OFFSET 21 ROWS FETCH NEXT 10 ROWS ONLY
		 *  */
		
		int offset = (pi.getCurrentPage() -1 ) * pi.getBoardLimit();
		int limit = pi.getBoardLimit();
		//RowBounds rowBounds = new RowBounds(offset, limit);		
		//return sqlSession.selectList("boardMapper.selectList" , paramMap ,rowBounds);
		
		paramMap.put("offset",offset);
		paramMap.put("limit",limit);
		return sqlSession.selectList("boardMapper.selectListByOffset" , paramMap);
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

	
	
	
	
	
	
	
	@Override
	public List<BoardType> selectBoardTypeList() {
		return sqlSession.selectList("boardMapper.selectBoardTypeList");
	}
	
	
}
