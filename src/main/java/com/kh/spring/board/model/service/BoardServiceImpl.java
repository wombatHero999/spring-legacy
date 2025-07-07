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
	
	@Transactional(rollbackFor = {Exception.class}) // 예외(Exception)가 발생하면 무조건 rollback처리 해주는 속성
	@Override
	public int insertBoard(Board b , List<BoardImg> imgList) throws Exception {
		/* 
		 * 1) 게시글 삽입
		 * 게시글 삽입시 게시글의 제목, 내용에 들어가는 문자열에 크로스사이트스크립트 공격을 방지하기위한 메소드 추가.
		 * 텍스트 에리어에 들어가는 엔터 및 스페이스바를 개행문자로 변환처리
		 * */
		b.setBoardContent(Utils.XSSHandling(b.getBoardContent()));
		b.setBoardContent(Utils.newLineHandling(b.getBoardContent()));
		b.setBoardTitle(Utils.XSSHandling(b.getBoardTitle()));
		
		int result = boardDao.insertBoard(b);
		// 2) 첨부파일 이미지리스트 등록(BOARD_IMG)
		int boardNo = b.getBoardNo();
//		버전1)
//		if(  !   imgList.isEmpty()) {
//			for( BoardImg bi    :  imgList) {
//				bi.setRefBno(boardNo);
//				result *= boardDao.insertBoardImg(bi);
//			}
//		}
//		버전2)
		if(!imgList.isEmpty()) {
			for(BoardImg bi : imgList) {
				bi.setRefBno(boardNo);
				
			}
			result = boardDao.insertBoardImgList(imgList); //다중 insert문
			
			if(result != imgList.size()) { //이미지 삽입 실패시
				throw new Exception("예외 강제 발생");
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
	public int updateBoard(Board board , String deleteList , MultipartFile upfile, List<MultipartFile> upfiles) throws Exception {
		
		board.setBoardContent(Utils.XSSHandling(board.getBoardContent()));
		board.setBoardContent(Utils.newLineHandling(board.getBoardContent()));
		board.setBoardTitle(Utils.XSSHandling(board.getBoardTitle()));
		
		//게시글 업데이트
		int result = boardDao.updateBoard(board);
		
		//이미지 및 첨부파일 등록
		String webPath = "/resources/images/board/"+board.getBoardCd()+"/";
		String serverFolderPath = application.getRealPath(webPath);
		
		if(result > 0) {
			// 업로드된 이미지들 분류작업.
			List<BoardImg> imgList = new ArrayList();
			
			if(upfiles != null) { // 일반게시판,자유게시판에서는 upfiles가 null임.
				for(int i =0; i<upfiles.size(); i++) {
					if(!upfiles.get(i).isEmpty()) {
						
						String changeName = Utils.saveFile(upfiles.get(i), serverFolderPath);
						
						// BoardImg객체 생성후, 필요한값들 추가해서 imgList에 추가.
						BoardImg bi = new BoardImg();
						bi.setChangeName(changeName);
						bi.setOriginName(upfiles.get(i).getOriginalFilename());
						bi.setRefBno(board.getBoardNo());
						bi.setImgLevel(i);
						
						imgList.add(bi);
					}
				}
			}
			// x버튼을 눌러서 이미지를 삭제하고자 하는 경우
			if(deleteList != null && !deleteList.equals("")) {
				//삭제하기위해서는 board_img_no가 필요함
				
				result = boardDao.deleteBoardImg(deleteList);
			}
			
			// db에서 삭제처리 완료 했거나 혹은 게시판 업데이트 성공시
			if(result > 0) {
				// BoardImg객체 하나하나 업데이트 .
				
				for( BoardImg bi  : imgList) {
					result = boardDao.updateBoardImg(bi);
					
					//result값은 1 혹은 0으로 반환
					// result == 0 ? 실패 -> 기존에 이미지가 없던 경우.
					// result == 1 ? 성공 -> 기존에 이미지가 있던 경우.
					if(result == 0) {
						result = boardDao.insertBoardImg(bi);
					}
				}
				
			}
		}
		
		throw new Exception("dd");
		//return result;
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
