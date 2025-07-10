package com.kh.spring.board.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.board.model.dao.BoardDao;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.board.model.vo.BoardImg;
import com.kh.spring.board.model.vo.BoardType;
import com.kh.spring.common.Utils;
import com.kh.spring.common.model.vo.PageInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

	private final BoardDao boardDao;
	
	@Override
	public Map<String, String> getBoardTypeMap() {
		return boardDao.getBoardTypeMap();
	}
	

	@Override
	public int selectListCount(Map<String, Object> paramMap) {
		return boardDao.selectListCount(paramMap);
	}

	@Override
	public List<Board> selectList(PageInfo pi, Map<String, Object> paramMap) {
		return boardDao.selectList(pi, paramMap);
	}
	
	@Override
	@Transactional(rollbackFor = {Exception.class})
	/* 
	 * @Transactional
	 *  - 선언적 트랜잭션 관리용 어노테이션
	 *  - 예외가 발생하면 무조건 rollback처리 한다.
	 *  - rollbackFor를 지정하지 않으면 RuntimeException에러가 발생한 경우만 
	 *    rollback한다.
	 * */
	public int insertBoard(Board b, List<BoardImg> imgList){
		/*
		 *  0. 게시글 데이터 전처리(개행문자 처리 및 xss공격 핸들링)
		 *  1. 게시글 테이블에 데이터를 먼저 추가
		 *  2. 첨부파일 테이블에 데이터 추가
		 *  3. 첨부파일 및 테이블 등록 실패시 롤백(에러 반환)
		 *  */
		// 데이터 전처리
		//  - 게시글 내용 : XSS핸들링 및 개행문자 처리
		//  - 게시글 제목 : XSS핸들링
		b.setBoardContent(Utils.XSSHandling(b.getBoardContent()));
		b.setBoardContent(Utils.newLineHandling(b.getBoardContent()));
		b.setBoardTitle(Utils.XSSHandling(b.getBoardTitle()));
		
		// 게시글 저장
		//  - mybatis의 selectKey기능을 이용하여 boardNo값을 b객체에 바인딩
		int result = boardDao.insertBoard(b);
		
		if(result == 0) {
			throw new RuntimeException("게시글 등록 실패");
		}
		
		// 첨부파일 등록
		//  - 전달받은 imgList가 비어있지 않은 경우 진행
		//  - 게시글번호를 추가로 refBno필드에 바인딩
		if(!imgList.isEmpty()) {
			for(BoardImg bi : imgList) {
				bi.setRefBno(b.getBoardNo());
			}
			// 다중 인서트문 실행
			int imgResult = boardDao.insertBoardImgList(imgList);
			
			if(imgResult != imgList.size()) {
				throw new RuntimeException("첨부파일 등록 실패");
			}
		}

		return result;
	}

	@Override
	public BoardExt selectBoard(int boardNo) {
		return boardDao.selectBoard(boardNo);
	}

	@Override
	public int increaseCount(int boardNo) {
		return boardDao.increaseCount(boardNo);
	}

	@Override
	public List<BoardImg> selectBoardImgList(int boardNo) {

		return null;
	}
	
	@Transactional(rollbackFor = {Exception.class})
	@Override
	public int updateBoard(Board board, String deleteList, List<BoardImg> imgList) {
//	    1. 게시글 수정
//       1) XSS, 개행 처리 후 추가
		board.setBoardContent(Utils.XSSHandling(board.getBoardContent()));
		board.setBoardContent(Utils.newLineHandling(board.getBoardContent()));
		board.setBoardTitle(Utils.XSSHandling(board.getBoardTitle()));
		
		int result = boardDao.updateBoard(board);
		if(result == 0) throw new RuntimeException("게시글 수정 실패");
		
//      2. 첨부파일 수정 -> INSERT, UPDATE, DELETE
//       1) 새롭게 등록한 첨부파일이 없는 경우 -> 아무것도 하지 않음
//       2) 첨부파일이 없던 게시글에 새롭게 추가한 경우 -> INSERT
//       3) 첨부파일이 있던 게시글에 새롭게 추가한 경우 -> UPDATE
//       4) 첨부파일이 있던 게시글에 첨부파일은 삭제한 경우 -> DELETE
//        - 사용하지 않게 된 첨부파일에 대해서는 고려하지 않아도 상관 없음.(스케쥴러를 통해 정리예정)
		
		if(deleteList != null && !deleteList.equals("")) {
			result = boardDao.deleteBoardImg(deleteList);
			
			if(result == 0) throw new RuntimeException("첨부파일 삭제 에러");			
		}
		
		if(!imgList.isEmpty()) {
			for(BoardImg bi : imgList) {
				if(bi.getBoardImgNo() == 0) {
					result = boardDao.insertBoardImg(bi);
				} else {
					result = boardDao.updateBoardImg(bi);
				}
				
				if(result == 0) {
					throw new RuntimeException("첨부파일 수정 실패");
				}				
			}
		}
		
		return result;
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
