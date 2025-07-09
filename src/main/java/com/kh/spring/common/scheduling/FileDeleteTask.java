package com.kh.spring.common.scheduling;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.BoardType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class FileDeleteTask {

	/*
	 * 파일 삭제 스케쥴러
	 *  - 목표 : DB에는 존재하지 않으나 WEB-SERVER상에 존재하는 쓸모없는 파일을 삭제.
	 * 업무로직
	 * 1. 데이터베이스(board_img 테이블)에 등록된 모든 이미지 파일 경로 목록을 조회
	 * 2. 모든 게시판 유형(boardType)을 조회하여, 각각의 게시판 디렉토리 경로를 탐색 
	 * 3. 해당 디렉토리에서 실제 서버에 존재하는 이미지 파일 목록 을 수집
	 * 4. 각 파일이 DB에 등록되어 있는 파일인지 여부를 판단
	 * 5. DB에 없는 파일(즉, 더 이상 사용되지 않는 파일)이라면 삭제 처리
	 * 6. 유저활동량이 적은 매달 1일 4시에 실행되도록 설정
	 */

	@Autowired
	private ServletContext application; // 정적파일들의 경로를 얻어오기위해 추가

	@Autowired
	private BoardService boardService;

	@Scheduled(cron = "0 4 1 * * *")
	public void deleteFile() {
		log.info("파일삭제 스케쥴러 시작");
		// 1) board_img안에 있는 모든 파일 목록 조회.
		List<String> list = boardService.selectFileList();
		log.info("list {}", list);
		// new File().listFiles(); --> 특정폴더 아래에 존재하는 모든파일을
		// file배열형태로 반환

		List<BoardType> boardTypeList = boardService.selectBoardTypeList();
		int count = 0;

		for (BoardType bt : boardTypeList) {
			// 2) /resources/images/board/T/에 있는 모든 이미지 파일목록 조회
			File path = new File(application.getRealPath("/resources/images/board/" + bt.getBoardCd() + "/"));
			File[] files = path.listFiles();// 현재 디렉토리안에 존재하는 모든 파일을 배열형태로 반환

			List<File> fileList = Arrays.asList(files);
			if (!list.isEmpty()) {
				for (File serverFile : fileList) {
					String fileName = serverFile.getName(); // 파일명 반환
					fileName = "/resources/images/board/" + bt.getBoardCd() + "/" + fileName;

					// List.indexOf(value) : List안에서 value과 같은값이 있으면 해당 값의 인덱스
					// 위치를 반환해주는 함수
					if (list.indexOf(fileName) == -1) {
						// select한 db목록에는 존재하지 않지만, 웹서버상에서는 저장된 파일인 경우.
						// -> 사용하고 있지 않은 파일로 간주.
						log.info(fileName + "을 삭제합니다.");
						count++;
						serverFile.delete();
					}
				}
			}
		}

		log.info("총 {} 개의 파일이 삭제됨", count);
		log.info("파일삭제 스케쥴러 종료");
	}

}
