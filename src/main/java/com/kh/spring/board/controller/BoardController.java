package com.kh.spring.board.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
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
import com.kh.spring.security.model.vo.MemberExt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // 로그
@Controller // bean객체 등록
@RequestMapping("/board")
@RequiredArgsConstructor
// # 1. 공통주소설정
// 현재 컨트롤러 호출시 /spring/board의 경로로 들어오는 모든 url요청을 받아줌
// 하위 메서드에서는 /board/list의 경우 /list만 작성하면 된다. 
public class BoardController {

	private final BoardService boardService;

	// ServletContext : application scope를 가진 서블릿 전역 객체.
	private final ServletContext application;

	// ResourceLoader
	// - 스프링에서 제공하는 리소스 로딩 클래스.
	// - classpath, file시스템, url자원 등 서로 다른 자원을 하나의 인터페이스로 일관되게 로드하는
	// - 메서드를 제공
	private final ResourceLoader resourceLoader;

	// #2. BoardTypeMap전역객체 설정.
	// 어플리케이션 전역에서 사용하는 boardType을 관리하는 Map객체 추가
	// PostConstruct
	// - 생성자 호출 이후 실행되는 초기화 로직.
	// - javax.annotation-api 의존성 추가 필요.
	@PostConstruct
	public void init() {
		// appplication에 boardTypeMap초기화
		// key=value , BOARD_CODE=BOARD_NAME
		Map<String, String> boardTypeMap = boardService.getBoardTypeMap();
		application.setAttribute("boardTypeMap", boardTypeMap);
		// System.out.println(boardTypeMap); {P=BoardType(boardCd=P, boardName=사진게시판),
		// N=BoardType(boardCd=N, boardName=일반게시판)}
	}

	// #3. 게시판 목록보기 서비스
	// 일반게시판, 사진게시판, 기타등등 게시판 등 하나의 메서드에서 여러 경로를 동시에 매핑하는 방법
	// 1. GetMapping의 속성값을 문자열 배열형태로 관리
	// @GetMapping({"/list/N","/list/P"}) // 단, 게시판 유형이 100개라면 100개 하드코딩
	// 2. GetMapping에 동적경로 변수를 사용
	@GetMapping("/list/{boardCode}")
	// - {boardCode}는 N,P,C,D,E,...등 동적으로 바뀌는 모든 자원경로를 저장함
	// - 동적자원경로는 @PathVariable로 추출하여 사용할 수 있음.
	// - @PathVariable로 등록한 변수는 자동으로 requestScope에 저장
	public String selectList(@PathVariable("boardCode") String boardCode,
			// currentPage
			// - 페이징처리에 필요한 변수.
			// - 현재 요청한 페이지의 값을 바인딩하며, 기본값은 1로하여 값을 전달하지 않은 경우
			// 항상 1페이지로 요청하게 만듬.
			@RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
			// @RequestParam Map<String,Object>
			// - 클라이언트의 요청 파라미터로 전송한 KEY,VALUE를 map형태로 담아주는 방법
			// - 파라미터의 개수가 정해져 있지 않거나, 일반적인 VO타입의 커맨드객체를 바인딩하지 않을 때 사용
			// EX)검색 파람 등
			// - @RequestParam생략시 바인딩불가
			@RequestParam Map<String, Object> paramMap, Model model) {
		// 업무로직
		// 1. 페이징처리
		// 1) 현재 요청한 게시판 코드 및 검색정보와 일치하는 게시글의 총 갯수를 조회
		// 2) 게시글갯수와, 페이징처리를 위한 기본 파람을 추가하여 PageInfo객체 생성
		// 2. 현재 요청한 게시판 코드와 일치하며, 현재 페이지에 해당하는 게시글 정보를 조회
		// 3. 게시글 목록페이지로 게시글정보와, 페이징 정보, 검색정보를 담아 forward
		// 1) 게시글갯수 카운팅
		// - 게시판 코드 + 검색조건을 동시에 보냄
		paramMap.put("boardCode", boardCode);
		int listCount = boardService.selectListCount(paramMap);
		int pageLimit = 10; // 페이지에 보여줄 게시글 갯수
		int boardLimit = 5; // 페이지당 최대 페이지바 개수

		// 2) PageInfo 생성
		// - 페이징 템플릿을 이용
		PageInfo pi = Pagination.getPageInfo(listCount, currentPage, pageLimit, boardLimit);
		// 검색결과+boardCode와 pageInfo객체를 넘겨 게시글 생성

		List<Board> list = boardService.selectList(pi, paramMap);
		// model영역에 데이터 추가

		model.addAttribute("list", list);
		model.addAttribute("pi", pi);
		model.addAttribute("param", paramMap);

		return "board/boardListView";
	}

	// #4. 게시판 등록 서비스
	// - 프론트 폼 추가
	// - pom.xml에 의존성 2개 추가 및 root-context.xml에 multipartResolver 설정
	// - multipart형식의 데이터 처리를 위한 filter등록.
	@GetMapping("/insert/{boardCode}")
	public String enrollBoard(@ModelAttribute Board b, @PathVariable("boardCode") String boardCode, Model model) {
		model.addAttribute("b", b);
		return "board/boardEnrollForm";
	}

	// #5. 게시판 등록기능
	// - multipart/form-data의 동작방식
	// - MultipartFile?
	@PostMapping("/insert/{boardCode}")
	public String insertBoard(@ModelAttribute Board b, @PathVariable("boardCode") String boardCode,
			Authentication authentication, // board테이블에 추가할 회원번호 얻어오기 위함
			Model model, RedirectAttributes ra,
			// List<MultipartFile> upfiles
			// - MultipartFile
			// - multipart/form-data방식으로 전송된 파일 데이터를 바인딩할 때 필요한 클래스
			// - 파일의 이름, 크기 ,존재여부, 저장 기능등 다양한 기능을 제공.
			// - List타입은 동일한 name속성값(value="upfile")으로 전달되는 파라미터를 하나의 컬렉션으로 바인딩해준다.
			// - 이때 바인딩할 객체가 없더라도 객체자체는 항상 생성한다.
			// - 사진게시판이든, 일반게시판이든 일관된 처리를 위해 배열형태로 선언.
			@RequestParam(value = "upfile", required = false) List<MultipartFile> upfiles) {
		// 업무로직
		// 1. 유효성검사(생략)
		// 2. 첨부파일이 존재하는지 확인
		// 1) 존재한다면 첨부파일을 web서버상에 저장 및 DB로 관리
		// 2) 존재하지 않는다면 이후 로직 진행
		// 3. 첨부파일 정보와, 게시글 정보를 바탕으로 게시글 등록 서비스 호출
		// 1) 게시글 등록을 위한 추가 정보 바인딩
		// 4. 게시글 등록 결과에 따른 페이지 지정
		// 1) 성공시 게시글 목록으로 리다이렉트
		// 2) 실패시 에러페이지로 포워딩 -> ControllerAdvice로 전역 처리

		// 2) 첨부파일 존재여부 체크
		// - 첨부파일이 존재한다면 web서버상에 첨부파일을 저장
		// - 첨부파일 관리를 위해 DB에 파일 위치정보 저장필요
		// - BOARD_IMG객체 생성후 파일의 저장경로, 원본명 저장.
		List<BoardImg> imgList = new ArrayList<>(); // 배열형태의 BoardImg객체를 저장할 imgList
		if (upfiles != null) {
			int level = 0; // 첨부파일의 레벨. 0은 썸네일 혹은 첨부파일을 의미함.
			// 사진게시판에서 썸네일파일과 아닌 파일을 구분하기 위해 사용.
			for (MultipartFile upfile : upfiles) {
				if (upfile.isEmpty()) {
					continue;
				}
				String changeName = Utils.saveFile(upfile, application, boardCode);
				BoardImg bi = new BoardImg();
				bi.setOriginName(upfile.getOriginalFilename());
				bi.setChangeName(changeName);
				bi.setImgLevel(level++);
				imgList.add(bi);
			}
		}
		// 3. 게시글 등록 서비스 호출
		// - 서비스 호출 전, 게시글 정보 바인딩 필요
		// - 전달받은 데이터 : 게시판 제목, 내용, 첨부파일
		// - 테이블에 추가를 위해 필요한 데이터 : 회원번호, 게시판코드

		// authentication에서 회원정보 추출하여 board객체에 바인딩
		Member loginUser = (Member) authentication.getPrincipal();
		b.setBoardWriter(String.valueOf(loginUser.getUserNo()));
		b.setBoardCd(boardCode);

		// 정보 체크
		log.debug("board {}", b);
		log.debug("imgList {}", imgList); // ref_bno값은 게시글 등록후 추가 가능

		int result = boardService.insertBoard(b, imgList);

		// 4. 게시글 등록 결과에 따른 페이지 지정
		// 1) 성공시 게시글 목록으로 리다이렉트
		// 2) 실패시 에러페이지로 포워딩 -> ControllerAdvice로 에러처리 보내기
		if (result <= 0) {
			throw new RuntimeException("게시글 작성 실패");
		}

		ra.addFlashAttribute("alertMsg", "게시글작성 성공");
		return "redirect:/board/list/" + boardCode;
	}

	@GetMapping("/detail/{boardCode}/{boardNo}")
	public String selectBoard(
			@PathVariable("boardCode") String boardCode,
			@PathVariable("boardNo") int boardNo,
			Authentication authentication,
			Model model, 
			// 사용자 인증을 위한 쿠기 가져오기
			// 1. HttpServletRequest을 이용하는 방법
			//    getCookies(); // 사용자가 가진 모든 쿠키목록을 반환. 
			//    내가 지정한 쿠리를 검색하기 위해 반복문을 돌려야함.
			// 2. Spring의 @CookieValue
			//    value속성에 내가 찾고자 하는 쿠키를 추가시 자동 바인딩 가능
			@CookieValue(value = "readBoardNo", required = false) String readBoardNoCookie,
			HttpServletResponse res, // 쿠키를 담아주기위해 필요
			HttpServletRequest req
			) {
		// 업무로직
		// 1. boardNo를 사용하여 게시판정보 조회.
		// 2. 조회수 증가 서비스 호출
		// 3. 게시판정보를 model영역에 담은 후 상세페이지로 forward

		// 1) 게시판 정보 조회
		// - 게시판 상세보기jsp를 바탕으로 필요한 정보 조회
		// - 제목, 내용, 작성자, 작성일 , 조회수, 첨부파일n개(첨부파일 번호, 수정전 이름, 수정후 이름, 레벨)
		// - 조회결과가 존재하지 않으면 error처리(ControllerAdvice에게 위임)
		BoardExt b = boardService.selectBoard(boardNo);
		log.info("{}", b);

		if (b == null) {
			throw new RuntimeException("게시글이 존재하지 않습니다.");
			// 실무에선 다양한 커스텀 예외를 추가하여 throw된 예외별로 에러페이지를 서비스함.
		}

		// 2) 게시글이 존재하는 경우. 조회수 증가 서비스 호출
		//	게시글 조회수 증가 로직
		//	 - 보안성과 데이터 정확성을 강화한 조회수 증가 서비스
		//	 일반적인 게시판 서비스의 한계:
		//		- 1) 사용자가 게시글을 새로고침하거나 반복 조회할 때마다 조회수가 무한정 증가됨
		//		- 2) 본인이 작성한 게시글을 조회할 때도 조회수가 증가하여 부정확한 통계 발생
		//	해결 방안: "조회수 중복 증가 방지" 및 "작성자 본인 예외 처리"
		//	- 즉, 사용자가 어떤 게시글을 열람했는지 정보를 일시적으로 저장해야 함
		//  저장 방식:
		//  - DB에 저장: 모든 사용자와 게시글 열람 이력을 기록하려면 데이터가 폭증 (비효율적)
		//  - 쿠키에 저장: 클라이언트 브라우저에 사용자가 읽은 게시글 번호를 보관 (readBoardNo 쿠키)
		//    ex) readBoardNo=11/23/45 → 사용자가 11번, 23번, 45번 글을 읽었다는 의미
		//  구현 방식:
		//  1) 사용자가 게시글을 조회할 때 쿠키(readBoardNo)를 확인
		//   - 해당 게시글 번호가 없으면 조회수 증가, 있으면 증가 생략
		//  2) 작성자가 자신의 글을 볼 경우 조회수 증가 로직 자체를 건너뜀
		
		//	부가 처리:
		//	 - 쿠키는 특정 시간 동안만 유지되도록 설정 (ex. 1시간)
		//	 - 쿠키에 게시글 번호를 누적 저장 (중복 저장 방지)
		//	 - 조회수 증가 후 즉시 게시글 객체의 count 값을 동기화시켜 UI에 반영

		// List<BoardImg> imgList = boardService.selectBoardImgList(boardNo);
		// model.addAttribute("imgList", imgList);

		int userNo = 0;

		MemberExt loginUser = (MemberExt) authentication.getPrincipal();
		userNo = loginUser.getUserNo();
		
		// 게시글 작성자의 boardWriter와 세션에서 얻어온 userNo가 같지 않은 경우에만 조회수 증가
		if (Integer.parseInt(b.getBoardWriter()) != userNo) {
			boolean increase = false; // 조회수 증가를 위한 불린값 true인 경우 조회수 증가처리

			// readBoardNo라는 이름의 쿠키가 생성된적이 없을때(첫 조회시)
			if (readBoardNoCookie == null) {
				// 증가값 트루 
				increase = true;
				// readBoardNo쿠키에 저잘할 문자열 추가
				readBoardNoCookie = boardNo+"";
			} else {
				// readBoardNo라는 이름의 쿠기가 존재하는 케이스
				// 기존 쿠키값중에 중복되는 게시글번호가 없는경우, 조회수증가와 함께
				// 쿠키에 저장된 값중 현재 조회된 게시글번호를 추가해줘야함
				List<String> list =  Arrays.asList(readBoardNoCookie.split("/")); // 1/2/5/11 ... => [1,2,5,11]
				// 기존의 쿠키값들중 현재 게시글번호와 일치하는값이 없는 경우 =>첨보는 게시글인 경우
				if (list.indexOf(boardNo + "") == -1) {
					increase = true;
					readBoardNoCookie += "/" + boardNo; // 쿠기값에 현재 게시글번호 추가
				}
			}
			if (increase) {
				int result = boardService.increaseCount(boardNo);
				if (result > 0) {// 성공적으로 조회수 증가시
					b.setCount(b.getCount() + 1);
					// 새쿠키 생성
					Cookie newCookie = new Cookie("readBoardNo", readBoardNoCookie);
					newCookie.setPath(req.getContextPath()); // /spring경로에 쿠키 저장
					newCookie.setMaxAge(1 * 60 * 60); // 1시간만 유지
					res.addCookie(newCookie);
				}
			}
		}

		model.addAttribute("board", b);

		return "board/boardDetailView";
	}


	// 첨부파일 / 이미지 수정하기.
	// + 첨부파일이나 이미지 수정하는 경우, 웹서버에 저장된 파일 신경쓰지 말기. db정보만 바꿔주기
	@GetMapping("/fileDownload/{boardNo}")
	public ResponseEntity<Resource> fileDownload(@PathVariable("boardNo") int boardNo) {

		ResponseEntity<Resource> responseEntity = null;
		// db에서 board테이블에서 boardNo값과 일치하는 행의 파일정보 조회
		BoardExt b = boardService.selectBoard(boardNo);

		if (b.getOriginName() == null) {
			return ResponseEntity.notFound().build(); // 응답상태 404로 설정후 반환
		}

		// Resource객체로 파일 얻어오기
		String saveDirectory = application.getRealPath("/resources/images/board/" + b.getBoardCd() + "/");
		File downFile = new File(saveDirectory, b.getChangeName());// 디렉토리 경로(saveDirectory)상에서
		// 두번째 매개변수로 전달받은 파일을 찾아서 객체 생성
		Resource resource = resourceLoader.getResource("file:" + downFile);
		try {
			String filename = new String(b.getOriginName().getBytes("utf-8"), "iso-8859-1");
			// utf-8방식으로 인코딩된 데이터(문자열)을 iso-8859-1로 변환 -> http통신할때 인코딩방식을 iso-8859-1로 해야하기
			// 때문.

			responseEntity = ResponseEntity.ok()
					// 내가 넘겨주고자하는 데이터가 바이너리형식의 데이터임을 의미.
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
					// CONTENT_DISPOSITION : 파일을 첨부파일 형태로 처리하겠다는 의미(다운로드)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename).body(resource);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return responseEntity;
	}
	
	// 일반게시판 수정하기기능(실습문제)
	@GetMapping("/update/{boardCode}/{boardNo}")
	public String updateBoard(@PathVariable(value = "boardCode") String boardCode, @PathVariable("boardNo") int boardNo,
			Model model) {
		// 업무로직을 생각 한 후 순서에 맞춰 구현해보기
		
		// 게시글정보(selectBoard), 첨부파일정보 조회후 함께 전달.
		BoardExt board = boardService.selectBoard(boardNo);
		// 게시글내용(boardContent)은 개행처리된 상태이기 때문에 <br>태그를 /n로 변경해주는 작업이 필요.
		board.setBoardContent(Utils.newLineClear(board.getBoardContent()));

		// PathVariable로 자동으로 request에 등록됨
//		model.addAttribute("boardCode", boardCode);
//		model.addAttribute("boardNo", boardNo);

		model.addAttribute("board", board);

		return "board/boardUpdateForm";
	}

	@PostMapping("/update/{boardCode}/{boardNo}")
	public String updateBoard2(Board board, @PathVariable("boardCode") String boardCode,
			@PathVariable("boardNo") int boardNo,
			// 리다이렉트할때 request스코프에 저장시킬 데이터를 담아주는 변수
			RedirectAttributes ra, Model model,
			// 첨부파일
			@RequestParam(value = "upfile", required = false) MultipartFile upfile,
			// 이미지파일들
			@RequestParam(value = "upfiles", required = false) List<MultipartFile> upfiles, String deleteList // [1,2,3]
	) {

		// 1) Board테이블 정보수정 => Update
		board.setBoardNo(boardNo);
		board.setBoardCd(boardCode);
		log.info("board ?? {} deleteList ?? {} ", board, deleteList); // boardNo, boardTitle, boardContent

		int result = 0;
		try {
			result = boardService.updateBoard(board, deleteList, upfile, upfiles);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 2) 이미지파일들 정보 수정 => UPDATE ,INSERT, DELETE
		// 사진이 없던곳에 새롭게 추가된경우 -> INSERT
		// 사진이 있던곳에 새롭게 추가된경우 -> UPDATE
		// 사진이 있던곳에 삭제가 된경우 -> DELETE
		// 사진이 있거나,없던곳에 그대로 없는경우 -> X

		// 3) 작업결과에 따른 페이지 지정
		if (result > 0) {
			// board/list/C
			// board/update/C/6
			// 수정 성공시 list로 리다이렉트 되도록 설정(상대경로방식으로)
			ra.addFlashAttribute("alertMsg", "게시글 수정 성공 ㅎㅎ");
			return "redirect:../../list/" + boardCode;
		} else {
			model.addAttribute("errorMsg", "게시글 수정 실패");
			return "common/errorPage";
		}
	}

}
