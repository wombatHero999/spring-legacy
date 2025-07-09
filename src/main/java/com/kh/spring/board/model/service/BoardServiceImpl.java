package com.kh.spring.board.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BoardServiceImpl implements BoardService{
	
	@Autowired
	private BoardDao boardDao;
	
	@Autowired
	private ServletContext application;

	@Override
	public int selectListCount(Map<String, Object> paramMap) {
		return boardDao.selectListCount(paramMap);
	}

	@Override
	public List<Board> selectList(PageInfo pi, Map<String, Object> paramMap) {
		return boardDao.selectList(pi , paramMap);
	}
	
	// @Transactional
	//  - 선언적 트랜잭션 관리 어노테이션
	//  - 예외(Exception)가 발생하면 무조건 rollback처리 해주는 속성(기본은 RuntimeException)
	@Transactional(rollbackFor = {Exception.class}) 
	@Override
	public int insertBoard(Board b , List<BoardImg> imgList)  {
		/*
		 * 서비스 로직 개요
		 * 0. 게시글 데이터 전처리(개행처리 및 XSS공격 핸들링)
		 * 1. 게시글 테이블 등록(항상실행)
		 * 2. 첨부파일/이미지 등록
		 * 3. 이미지 및 테이블 등록 실패시 롤백처리
		 *  */
		
		/* 
		 * 0) 데이터 전처리
		 *   - 게시글 내용에 대한 XSS 공격 방지 및 개행 문자 처리
		 *   - 사용자가 입력한 <script> 같은 태그를 무력화하여 보안 강화(게시글 제목, 내용)
		 *   - textarea의 개행(\r\n)을 <br>로 치환하여 출력 시 줄바꿈 유지(게시글 내용)
		 * */
		b.setBoardContent(Utils.XSSHandling(b.getBoardContent()));
		b.setBoardContent(Utils.newLineHandling(b.getBoardContent()));
		b.setBoardTitle(Utils.XSSHandling(b.getBoardTitle()));
		
		// 1) 게시글 저장
		//  - 게시글테이블에 데이터 insert.
		//  - mybatis의 selectKey를 통해 boardNo값을 b객체에 바인딩
		
		int result = boardDao.insertBoard(b);
		
		// 3) 테이블 등록 실패시 롤백처리
		if(result == 0) {
			throw new RuntimeException("게시글 등록 실패");
		}
		
		// 2) 첨부파일 이미지리스트 등록(BOARD_IMG)
		//  - 전달받은 imgList가 존재하는 경우 등록 진행
		//  - 등록 성공한 게시판의 번호를 refBno에 추가로 저장.
		//  - imgList를 dao로 전달하여 insert문 실행
		//    (mybatis의 foreach태그를 활용하여 다중 insert문 구현)
		//    (배열의 갯수만큼 insert메서드를 호출하는 것보다 성능상 좋음.)
		if(!imgList.isEmpty()) {
			// refBno초기화
			for(BoardImg bi : imgList) {
				bi.setRefBno(b.getBoardNo());
			}
			// 다중 insert문 수행
			int imgResult = boardDao.insertBoardImgList(imgList);
			
			// 3. 이미지 등록 실패시 에러발생시켜 "롤백"처리
			//  - 추가된 행의 개수와 배열의 크기가 일치하지 않는 경우 실패
			if(imgResult != imgList.size()) {
				throw new RuntimeException("예외 강제 발생");
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
		return boardDao.selectBoardImgList(boardNo);
	}
	
	
	@Transactional(rollbackFor = {Exception.class})
	@Override
	public int updateBoard(Board board ,List<BoardImg> imgList, String deleteList) {
		// 1) XSS, 개행 처리 후 추가
		board.setBoardContent(Utils.XSSHandling(board.getBoardContent()));
		board.setBoardContent(Utils.newLineHandling(board.getBoardContent()));
		board.setBoardTitle(Utils.XSSHandling(board.getBoardTitle()));
		
		//게시글 업데이트
		int result = boardDao.updateBoard(board);
		
		if(result == 0) throw new RuntimeException("게시글 수정 실패");
		
		// 2) 이미지파일들 정보 수정 => UPDATE ,INSERT, DELETE
		// 사진이 없던곳에 새롭게 추가된경우 -> INSERT
		// 사진이 있던곳에 새롭게 추가된경우 -> UPDATE -> DELETE후 INSERT
		// 사진이 있던곳에 삭제가 된경우 -> DELETE
		// 사진이 있거나,없던곳에 그대로 없는경우 -> X
		
		
		// x버튼을 눌러서 이미지를 삭제하고자 하는 경우
		if(deleteList != null && !deleteList.equals("")) {
			result = boardDao.deleteBoardImg(deleteList);
		}
		if(!imgList.isEmpty()) {	
			// 새롭게 추가된 첨부파일이 존재하는 경우
			for( BoardImg bi  : imgList) {
				if(bi.getBoardImgNo() == 0) {// 새롭게 추가된 경우 insert
					result = boardDao.insertBoardImg(bi);
				}
				else {
					result = boardDao.updateBoardImg(bi);
				}
				
				if(result == 0) {
					throw new RuntimeException("게시글 수정 실패");
				}
			}
		}
		
		
		return result;
	}

	@Override
	public List<String> selectFileList() {
		return boardDao.selectFileList();
	}

	@Override
	public List<BoardType> selectBoardTypeList() {
		return boardDao.selectBoardTypeList();
	}

	@Override
	public Map<String, String> getBoardTypeMap() {
		return boardDao.getBoardTypeMap();
	}
	
	
	
	
}
