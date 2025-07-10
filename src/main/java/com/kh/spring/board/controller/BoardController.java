package com.kh.spring.board.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.board.model.service.BoardService;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;
import com.kh.spring.board.model.vo.BoardImg;
import com.kh.spring.common.Utils;
import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.common.template.Pagination;
import com.kh.spring.member.model.vo.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board") // 공용주소 설정
@Slf4j
public class BoardController {

	private final BoardService boardService;
	// ServletContext : application scope를 가진 서블릿 전역에서 사용가능한 객체
	private final ServletContext application;
	/*
	 * ResourceLoader - 스프링에서 제공하는 자원 로딩 클래스 - classpath,file시스템,url등 다양한 경로의 자원을
	 * 동일한 인터페이스로 로드하는 메서드를 제공
	 */
	private final ResourceLoader resourceLoader;

	// BoardType전역객체 설정
	// - 어플리케이션 전역에서 사용할 수 있는 BoardType객체를 추가
	// - 서버 가동중에 1회 수행되어 application에 자동으로 등록
	@PostConstruct
	public void init() {
		// key=value , BOARD_CODE=BOARD_NAME
		// N=일반게시판, P=사진게시판
		Map<String, String> boardTypeMap = boardService.getBoardTypeMap();
		application.setAttribute("boardTypeMap", boardTypeMap);
		log.info("boardTypeMap : {} ", boardTypeMap);
	}

	// 게시판 목록보기 서비스
	// 일반게시판, 사진게시판, 롤게시판 등등 모든 목록보기 페이지를 하나의 메서드에서 경로를 매핑하는 방법
	// 1. GetMapping의 속성값을 "문자열 배열"형태로 관리
	// @GetMapping({"/list/N","/list/P"})
	// 2. GetMapping에 동적 경로 변수를 사용
	@GetMapping("/list/{boardCode}")
	// - {boardCode}는 N,P,C,L,D..등 동적으로 바뀌는 모든 보드코드값을 저장할 수 있다
	// - 선언한 동적 경로 변수는 @PathVariable어노테이션으로 추출하여 사용할 수 있다.
	// - @PathVariable로 자원경로 추출시, 추출한 변수는 model영역에 자동으로 추가된다.
	public String selectList(
			@PathVariable("boardCode") String boardCode,
			// currentPage
			// - 현재 요청한 페이지 번호.(페이징 처리에 필요한 변수)
			// - 기본값은 1로 처리하여, 값을 전달하지 않은 경우 항상 1페이지로 요청하게 처리
			@RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
			/*
			 * @RequestParam Map<String,Object> - 클라이언트가 요청시 전달한 파라미터의 key, value을 Map형태로
			 * 만들어서 대입 - 현재 메서드로 전달할 파라미터의 개수가 정해져 있지 않은 경우, 일반적인 vo클래스로 바인딩되지 않는 경우 사용한다
			 * (ex. 검색파라미터 등) - 반드시 @RequestParam어노테이션을 추가해야 바인딩해준다.
			 */
			@RequestParam Map<String, Object> paramMap, Model model) {
		/*
		 * 업무로직
		 * 1. 페이징처리
		 * 	   1) 현재 요청한 게시판 코드 및 검색정보와 일치하는 게시글의 총 갯수를 조회
		 *     2) 게시글 갯수, 페이지 번호, 기본파라미터들을 추가하여 페이징정보(PageInfo)객체를 생성
		 * 2. 현재 요청한 게시판코드와 일치하면서, 현재 피이지에 해당하는 게시글정보를 조회
		 * 3. 게시글 목록페이지로 게시글정보, 페이징정보, 검색정보를 담아서 forward
		 */
		paramMap.put("boardCode",boardCode); // 검색조건 + 게시판 코드
		int listCount = boardService.selectListCount(paramMap);
		int pageLimit = 10;
		int boardLimit = 10;
		
		// 페이지 정보 템플릿을 이용하여 PageInfo 생성
		PageInfo pi = Pagination.getPageInfo(listCount, currentPage, pageLimit, boardLimit);
		
		List<Board> list = boardService.selectList(pi, paramMap);
		
		model.addAttribute("list",list);
		model.addAttribute("pi",pi);
		model.addAttribute("param", paramMap);

		return "board/boardListView";
	}
	
	// 게시판 등록폼 이동 서비스
	@GetMapping("/insert/{boardCode}")
	public String enrollForm(
			@ModelAttribute Board b, 
			@PathVariable("boardCode") String boardCode,
			Model model
			) {
		model.addAttribute("b", b);
		return "board/boardEnrollForm";
	}
	
	// 게시판등록기능
	@PostMapping("/insert/{boardCode}")
	public String insertBoard(
			@ModelAttribute Board b,
			@PathVariable("boardCode") String boardCode,
			Authentication auth,
			Model model, 
			RedirectAttributes ra,
			/* 
			 * List<MultipartFile> 
			 *  - MultipartFile
			 *  - multipart/form-data방식으로 전송된 파일데이터를 바인딩해주는 클래스
			 *  - 파일의 이름, 크기, 존재, 저장기능 다양한 메서드 제공
			 *  - name속성값이 upfile으로 전달되는 모든 파일 파람을 하나의 컬렉션으로 모아오기위해 선언
			 *  - @RequestParam + List/Map 사용시 바인딩할 데이터가 없더라도 항상 객체 자체는 생성된다.
			 * */
			@RequestParam(value="upfile" , required = false) List<MultipartFile> upfiles
			) {
		/*
		 *	업무로직
		 *	1. 유효성검사(생략)
		 *	2. 첨부파일이 존재하는지 확인
		 *      1) 존재한다면 첨부파일을 web서버상에 저장하는 로직 필요
		 *      2) 존재하지않는다면 3번로직 수행 
		 *  3. 게시판정보 등록 및 첨부파일정보 등록을 위한 서비스 호출
		 *  4. 게시글 등록 결과에 따른 페이지 지정
		 *      1) 성공시 목록으로 리다이레익트
		 *      2) 실패시 에러페이지로 포워딩 -> ControllerAdvice로 처리
		 *  */
		// 첨부파일 존재여부 체크
		List<BoardImg> imgList = new ArrayList<>();
		int level = 0; // 첨부파일의 레벨 
		// 0= 썸네일, 0이 아닌값들은 썸네일이 아닌 기타 파일들
		for(MultipartFile upfile : upfiles) {
			if(upfile.isEmpty()) {
				continue;
			}
			// 첨부파일이 존재한다면 WEB서버상에 첨부파일 저장
			// 첨부파일 관리를 위해 DB에 첨부파일의 위치정보를 저장 
			String changeName = Utils.saveFile(upfile, application, boardCode); 
			BoardImg bi = new BoardImg();
			bi.setChangeName(changeName);
			bi.setOriginName(upfile.getOriginalFilename());
			bi.setImgLevel(level++);
			imgList.add(bi); // 연관게시글번호 refBno값 추가 필요
		}
		
		// 게시글 등록 서비스 호출
		//  - 서비스 호출 전, 게시글 정보 바인딩
		//  - 테이블에 추가하기 위해 필요한 데이터 : 회원번호, 게시판 코드
		Member loginUser = (Member) auth.getPrincipal();
		b.setBoardWriter( String.valueOf(loginUser.getUserNo()));
		b.setBoardCd(boardCode);
		
		// 정보체크
		log.debug("board : {}" , b);
		log.debug("imgList : {}" , imgList);
		int result = boardService.insertBoard(b, imgList);
		
		// 게시글 등록 결과에 따른 페이지 지정
		if(result == 0) {
			throw new RuntimeException("게시글 작성 실패");
		}
		
		ra.addFlashAttribute("alertMsg","게시글 작성 성공");
		return "redirect:/board/list/"+boardCode;
	}
	
	// 게시판 상세보기
	@GetMapping("/detail/{boardCode}/{boardNo}")
	public String selectBoard(
			@PathVariable("boardCode") String boardCode,
			@PathVariable("boardNo") int boardNo,
			Authentication auth ,
			Model model,
			// 사용자의 쿠키 가져오기
			// 1. HttpServletRequest의 getCookies()
			// 2. Spring의 @CookieValue
			@CookieValue(value="readBoardNo", required = false) String readBoardNoCookie,
			HttpServletRequest req,
			HttpServletResponse res
		) {
		/*
		 * 업무로직
		 * 1. boardNo를 기반으로 게시판 정보 조회
		 * 2. 조회수 증가서비스 호출 
		 * 3. 게시판정보를 model영역에 담은 후 forward
		 *  */
		
		// 게시글 정보를 조회
		// 게시글정보에 사용자의 이름, 첨푸바일 목록을 추가로 담아서 반환
		// 하기위해 BoardExt사용
		BoardExt b = boardService.selectBoard(boardNo);
		log.debug("게시글 정보 : {}",b);
		
		if(b == null) {
			throw new RuntimeException("게시글이 존재하지 않습니다.");
		}
		
		/*
		 * 게시글이 존재하는 경우 조회수 증가 서비스 호출
		 * 게시글 조회수 증가 로직
		 *   일반적인 게시판 서비스
		 *     1) 사용자가 게시글을 새로고침하거나, 반복 조회시 조회수가 무한정 증가
		 *     2) 본인이 작성한 게시글을 조회할 때도 조회수가 증가
		 *   - 사용자가 어떤 게시글을 열람했는지 정보를 저장해두어야 한다.
		 *   저장방식
		 *   DB에 저장  : 모든 사용자의 게시글 열람기록을 관리하기에 비효율적
		 *   쿠키에 저장 : 클라이언트 브라우저에 사용자가 읽은 게시글 번호를 보관(readBoardNo)
		 *   ex) readBoardNo=11/12/13/14
		 *   + 쿠키의 유효시간 1시간동안 유지되도록 설정, /spring url에 쿠키를 보관
		 *  */
		
		int userNo = ((Member) auth.getPrincipal()).getUserNo();
		if(userNo != Integer.parseInt(b.getBoardWriter())) {
			boolean increase = false; // 조회수 증가를 위한 체크변수
			
			// readBoardNo라는 이름의 쿠키가 있는지 조사.
			if(readBoardNoCookie == null) {
				// 첫 조회
				increase = true;
				readBoardNoCookie = boardNo +"";
			}else {
				// 쿠키가 있는 경우
				List<String> list = Arrays.asList(readBoardNoCookie.split("/"));
				// 기존 쿠키값들 중 게시글번호와 일치하는 값이 하나도 없는 경우
				if(list.indexOf(boardNo+"") == -1) {
					increase = true;
					readBoardNoCookie += "/"+boardNo;
				}
			}
			
			if(increase) {
				int result = boardService.increaseCount(boardNo);
				if(result > 0) {
					b.setCount(b.getCount() + 1);
					
					// 새 쿠키 생성하여 클라이언트에게 전달
					Cookie newCookie = new Cookie("readBoardNo",readBoardNoCookie);
					newCookie.setPath(req.getContextPath());
					newCookie.setMaxAge(1 * 60 * 60); // 1시간
					res.addCookie(newCookie);
				}				
			}			
		}
		
		model.addAttribute("board",b);
		
		return "board/boardDetailView";
	}
	
	// 첨부파일 다운로드
	@GetMapping("/fileDownload/{boardNo}")
	public ResponseEntity<Resource> fileDownload(
			@PathVariable("boardNo") int boardNo){
		/* 
		 * 업무로직
		 * 1. 첨부파일 정보 조회(db)
		 * 2. 첨부파일의 changeName을 바탕으로 "웹서버"상의 첨부파일 로드
		 * 3. 로드한 첨부파일을 ResponseEntity를 통해 사용자에게 반환
		 * */
		//DB에서 게시글 및 첨부파일 정보 조회
		BoardExt b = boardService.selectBoard(boardNo);
		
		if(b.getImgList().isEmpty()) {
			return ResponseEntity.notFound().build();// 404
		}
		
		// Resource객체 얻어오기
		String changeName = b.getImgList().get(0).getChangeName();
		String realPath = application.getRealPath(changeName);
		File downFile = new File(realPath);
		
		if(!downFile.exists()) {
			return ResponseEntity.notFound().build();
		}
		
		Resource resource = resourceLoader.getResource("file:"+realPath);
		String filename = "";
		try {
			 filename = new String(b.getImgList().get(0).getOriginName().getBytes("utf-8"), "iso-8859-1" );
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
				.header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename="+filename)
				.body(resource);
	}
	
	// 게시판 수정기능(Get)
	@GetMapping("/update/{boardCode}/{boardNo}")
	public String updateBoard(
			@PathVariable("boardCode") String boardCode,
			@PathVariable("boardNo") int boardNo,
			Authentication authentication,
			Model model
			) {
		// 업무로직
		// 1. 현재 게시글을 수정할 수 있는 사용자인지 체크
		//     - 게시글의 작성자 == 로그인한 사용자
		//     - 관리자 권한
		BoardExt board = boardService.selectBoard(boardNo);
		if(board == null) {
			throw new RuntimeException("게시글이 존재하지 않습니다.");
		}
		
//		int boardWriter = Integer.parseInt(board.getBoardWriter());
//		int userNo = ((Member) authentication.getPrincipal()).getUserNo();
//		if(!(boardWriter == userNo || (authentication.getAuthorities().stream()
//				.anyMatch( authority -> authority.getAuthority().equals("ROLE_ADMIN") )) ) ) {
//			throw new RuntimeException("게시글 수정권한이 없습니다.");
//		}
		// 2. 게시글 정보 조회
		//     - newlineClear메서드를 통해 개행문자 원상복구
		board.setBoardContent(Utils.newLineClear(board.getBoardContent()));// <br> -> \n
		
		// 3. model영역에 추가후 forward
		model.addAttribute("board",board);
		
		return "board/boardUpdateForm";
	}
	
	@PostMapping("/update/{boardCode}/{boardNo}")
	public String updateBoard2(
			@ModelAttribute Board board,
			@PathVariable("boardCode") String boardCode,
			@PathVariable("boardNo") int boardNo,
			Authentication authentication,
			RedirectAttributes ra, 
			Model model,			
			@RequestParam(value = "upfile", required = false) List<MultipartFile> upfiles,			
			String deleteList ,
			@RequestParam(value = "imgNo", required = false) List<Integer> imgNoList
	) {
		// 다음 업무로직의 순서에 맞춰 코드를 작성
		// 0. 유효성검사(생략)
		// 1. 현재 게시글을 수정할 수 있는 사용자인지 체크(생략)
		// 2. 새롭게 등록한 첨부파일이 있는지 체크 후 저장
		List<BoardImg> imgList = new ArrayList<>();
		for(int i =0 , j =0; i<imgNoList.size(); i++) {
			if(!(j < upfiles.size())) { // upfiles로 꺼낼 값이 없는 경우
				break;
			}
			MultipartFile upfile = upfiles.get(j++);
			if(upfile.isEmpty()) {
				continue;
			}
			String changeName = Utils.saveFile(upfile, application, boardCode); 
			BoardImg bi = new BoardImg();
			bi.setBoardImgNo(imgNoList.get(i)); // 현재레벨에 맞는 imgNo
			bi.setChangeName(changeName);
			bi.setOriginName(upfile.getOriginalFilename());
			bi.setImgLevel(i);
			bi.setRefBno(boardNo); // 게시글 번호 추가
			imgList.add(bi); 
		}
		board.setBoardNo(boardNo);
		board.setBoardCd(boardCode);
		
		log.debug("board : {}" , board);
		log.debug("imgList : {}", imgList);
		log.debug("deleteList : {}",deleteList);
		
		// 3. 게시글 , 첨부파일 수정 서비스 요청
		int result = boardService.updateBoard(board, deleteList, imgList);
		//    1) UPDATE에 필요한 데이터를 추가로 바인딩
		//    서비스 내부 로직
		//    1. 게시글 수정
		//       1) XSS, 개행 처리 후 추가
		//    2. 첨부파일 수정 -> INSERT, UPDATE, DELETE
		//       1) 새롭게 등록한 첨부파일이 없는 경우 -> 아무것도 하지 않음
		//       2) 첨부파일이 없던 게시글에 새롭게 추가한 경우 -> INSERT
		//       3) 첨부파일이 있던 게시글에 새롭게 추가한 경우 -> UPDATE
		//       4) 첨부파일이 있던 게시글에 첨부파일은 삭제한 경우 -> DELETE
		//        - 사용하지 않게 된 첨부파일에 대해서는 고려하지 않아도 상관 없음.(스케쥴러를 통해 정리예정)
		
		
		// 3. 처리 결과에 따라 응답 페이지 지정 
		//    1) 실패시 에러반환
		//    2) 성공시 작업했떤 view페이지로 redirect	
		ra.addFlashAttribute("alertMsg","게시글 수정 성공");
		return "redirect:/board/detail/"+boardCode+"/"+boardNo;
	}
	
	
	
	
	
	
	
	
	

}














