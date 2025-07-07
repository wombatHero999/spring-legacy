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
import javax.servlet.http.HttpSession;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
import com.kh.spring.board.model.vo.BoardType;
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
	//  - 스프링에서 제공하는 리소스 로딩 클래스.
	//  - classpath, file시스템, url자원 등 서로 다른 자원을 하나의 인터페이스로 일관되게 로드하는
	//  - 메서드를 제공
	private final ResourceLoader resourceLoader;
	
	// #2. BoardTypeMap전역객체 설정.
	// 어플리케이션 전역에서 사용하는 boardType을 관리하는 Map객체 추가 
	// PostConstruct
	//  - 생성자 호출 이후 실행되는 초기화 로직.
	//  - javax.annotation-api 의존성 추가 필요.
	@PostConstruct
    public void init() {
		// appplication에 boardTypeMap초기화
		// key=value , BOARD_CODE=BOARD_NAME
		Map<String, String> boardTypeMap = boardService.getBoardTypeMap();
        application.setAttribute("boardTypeMap", boardTypeMap);
        // System.out.println(boardTypeMap); {P=BoardType(boardCd=P, boardName=사진게시판), N=BoardType(boardCd=N, boardName=일반게시판)}
    }
	
	// 일반게시판, 사진게시판, 기타등등 게시판 등 하나의 메서드에서 여러 경로를 동시에 매핑하는 방법
	// 1. GetMapping의 속성값을 문자열 배열형태로 관리
	// @GetMapping({"/list/N","/list/P"}) // 단, 게시판 유형이 100개라면 100개 하드코딩
	// 2. GetMapping에 동적경로 변수를 사용
	@GetMapping("/list/{boardCode}") 
	//  - {boardCode}는 N,P,C,D,E,...등 동적으로 바뀌는 모든 자원경로를 저장함
	//  - 동적자원경로는 @PathVariable로 추출하여 사용할 수 있음.
	//  - @PathVariable로 등록한 변수는 자동으로 requestScope에 저장
	public String selectList(
			@PathVariable("boardCode") String boardCode ,
			// currentPage 
			//  - 페이징처리에 필요한 변수. 
			//  - 현재 요청한 페이지의 값을 바인딩하며, 기본값은 1로하여 값을 전달하지 않은 경우
			//    항상 1페이지로 요청하게 만듬.
			@RequestParam(value ="currentPage" , defaultValue ="1") int currentPage,
			// @RequestParam Map<String,Object>  
			//  - 클라이언트의 요청 파라미터로 전송한 KEY,VALUE를 map형태로 담아주는 방법
			//  - 파라미터의 개수가 정해져 있지 않거나, 일반적인 VO타입의 커맨드객체를 바인딩하지 않을 때 사용
			//    EX)검색 파람 등
			//  - @RequestParam생략시 바인딩불가 
			@RequestParam Map<String,Object> paramMap,
			Model model 
			) {
			// 업무로직
			// 1. 페이징처리
			//   1) 현재 요청한 게시판 코드 및 검색정보와 일치하는 게시글의 총 갯수를 조회
			// 	 2) 게시글갯수와, 페이징처리를 위한 기본 파람을 추가하여 PageInfo객체 생성
			// 2. 현재 요청한 게시판 코드와 일치하며, 현재 페이지에 해당하는 게시글 정보를 조회
			// 3. 게시글 목록페이지로 게시글정보와, 페이징 정보, 검색정보를 담아 forward
			// 1) 게시글갯수 카운팅
			//     - 게시판 코드 + 검색조건을 동시에 보냄 
			paramMap.put("boardCode", boardCode);
			int listCount = boardService.selectListCount(paramMap);
			int pageLimit = 10; // 페이지에 보여줄 게시글 갯수
			int boardLimit = 5; // 페이지당 최대 페이지바 개수
			
			// 2) PageInfo 생성 
			//     - 페이징 템플릿을 이용
			PageInfo pi = Pagination.getPageInfo(listCount, currentPage, pageLimit, boardLimit);
			// 검색결과+boardCode와 pageInfo객체를 넘겨 게시글 생성
			
			List<Board> list = boardService.selectList(pi, paramMap);			
			// model영역에 데이터 추가
			
			model.addAttribute("list" , list);
			model.addAttribute("pi" , pi);
			model.addAttribute("param", paramMap); 
			
		return "board/boardListView";
	}	
	
	// -- 2일차 끝
	
	// -- 3일차 게시판 등록서비스 + 상세서비스시작
	@GetMapping("/insert/{boardCode}")
	public String enrollBoard(@PathVariable("boardCode") String boardCode) {
		return "board/boardEnrollForm";
	}
	
	@PostMapping("/insert/{boardCode}")
	public String insertBoard(
			Board b ,
			@PathVariable("boardCode") String boardCode ,
			// sessionAttriubtes에 의해 session으로 이관된 데이터를 매개변수에서 얻어올때 사용하는 방법
			@AuthenticationPrincipal MemberExt loginUser, 
			Model model,
			RedirectAttributes ra, 
			// 첨부파일
			@RequestParam(value="upfile" , required = false ) MultipartFile upfile , 
			@RequestParam(value="upfiles" , required = false) List<MultipartFile> upfiles
			) {
		//이미지, 파일을 저장할 저장경로 얻어오기
		String webPath = "/resources/images/board/"+boardCode+"/";
		String serverFolderPath = application.getRealPath(webPath);
		
		//디렉토리가 존재하지 않는다면 생성해주는 코드 추가
		File dir = new File(serverFolderPath);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		// 사용자가 첨부파일을 등록한 경우 
		// upfile은 첨부파일이 있뜬, 없든 무조건 객체는 생성됨.
		// 단, 첨부파일 등록을 하지 않은경우 내부에 데이터가 비어있다.("")
		// 사용자가 전달한 파일이 있는지 없는지는 filename이 존재하는지로 확인하면 된다.
		if(upfile != null && !upfile.getOriginalFilename().equals("")) {
			String changeName = Utils.saveFile(upfile,serverFolderPath );
			b.setOriginName(upfile.getOriginalFilename());
			b.setChangeName(changeName);
		}
		
		//Board 객체에 데이터 추가
		b.setBoardWriter(loginUser.getUserNo()+"");
		b.setBoardCd(boardCode);
		
		log.info("board {}" , b );
		
		// 첨부파일목록(upfiles)같은경우 선택하고 안하고 상관없이 객체는 생성이 된다. 단, 길이가 0일수가 있음
		// 전달된 파일이 있을경우에만 해당파일을 웹서버에 저장하고, BOARD_IMG테이블에 해당정보를 등록할 예정.
		List<BoardImg> imgList = new ArrayList();
		
		log.info("imgList ?? {} " , upfiles); // 길이 4
		if(upfiles != null ) {
			for(int i =0; i<upfiles.size(); i++) {
				if(upfiles.get(i).getOriginalFilename().equals("")) {
					continue;
				}
				String changeName = Utils.saveFile(upfiles.get(i), serverFolderPath);
				BoardImg bi = new BoardImg();
				bi.setChangeName(changeName);
				bi.setOriginName(upfiles.get(i).getOriginalFilename());
				bi.setImgLevel(i);
				// pk, refBno
				imgList.add(bi);
			}
		}
		
		int result = 0;
		try {
			result = boardService.insertBoard(b , imgList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String url = "";
		if(result > 0) {
			//RedirectAttributes ra, //매개변수
			ra.addFlashAttribute("alertMsg", "글작성 성공함.");
			//session.setAttribute("alertMsg", "글작성 성공함.");			
			url = "redirect:/board/list/"+boardCode;
		}else {
			model.addAttribute("errorMsg","게시글 작성 실패함");
			url ="common/errorPage";
		}
		return url;
	}
	
	
	@GetMapping("/detail/{boardCode}/{boardNo}")
	public String selectBoard(
			@PathVariable("boardCode") String boardCode,
			@PathVariable("boardNo") int boardNo,
			Model model, 
			HttpServletRequest req , 
			HttpServletResponse res , 
			HttpSession session
			) {
		// 게시글 조회 -> 수정.
		// 게시판 정보 조회
		//Board b = boardService.selectBoard(boardNo);
		BoardExt b = boardService.selectBoard(boardNo);
		log.info("{}" , b);
		// 상세조회 성공시 쿠키를 활용해서 조회수가 중복으로 증가되지 않도록 방지
		// + 본인의 글은 애초에 조회수가 증가되지 않게끔 설정
		if(b != null) {
			
			//List<BoardImg> imgList = boardService.selectBoardImgList(boardNo);
			//model.addAttribute("imgList", imgList);
			
			int userNo = 0;
			
			Member loginUser = (Member) session.getAttribute("loginUser");
			
			if(loginUser != null) {
				userNo = loginUser.getUserNo();
			}
			
			//게시글 작성자의 boardWriter와 세션에서 얻어온 userNo가 같지 않은 경우에만 조회수 증가
			if( Integer.parseInt(b.getBoardWriter()) != userNo  ) {
				//쿠키
				Cookie cookie = null;
				
				Cookie[] cArr = req.getCookies(); // 사용자의 쿠키정보들 가져오기
				
				if(cArr != null && cArr.length > 0) {
					for(Cookie c : cArr) {
						//사용자의 쿠키 목록중, 쿠키의 이름이 "readBoardNo"라는걸 찾을예정
						if("readBoardNo".equals(c.getName())) {
							cookie = c;
							break;
						}
					}
				}
				
				int result = 0;
				
				// readBoardNo라는 이름의 쿠키가 생성된적이 없을때
				if(cookie == null) {
					// readBoardNo쿠키 생성
					cookie = new Cookie("readBoardNo",boardNo+"");
					// 조회수 증가 서비스 호출
					result = boardService.increaseCount(boardNo);
				}else {
					// readBoardNo라는 이름의 쿠기가 존재하는 케이스
					// 기존 쿠키값중에 중복되는 게시글번호가 없는경우, 조회수증가와 함께
					// 쿠키에 저장된 값중 현재 조회된 게시글번호를 추가
					
					String[] arr = cookie.getValue().split("/"); // 1/2/5/11 ... => [1,2,5,11]
					// 배열을 컬렉션(List)으로 변환 -> indexOf를 사용할 예정
					// List.indexOf(obj) : 컬렉션안에서 매개변수로 들어온 obj와 일치하는 부분의 인덱스를 반환해줌
					// 일치하는 값이 없다면? -1 반환
					List<String> list = Arrays.asList(arr);
					
					// 기존의 쿠키값들중 현재 게시글번호와 일치하는값이 없는 경우 =>첨보는 게시글인 경우
					if(list.indexOf(boardNo+"") == -1) {
						// 조회수 증가
						result = boardService.increaseCount(boardNo);
						// 쿠기값에 현재 게시글번호 추가
						cookie.setValue(cookie.getValue()+"/"+boardNo);
					}
				}
				
				if(result > 0) {// 성공적으로 조회수 증가시
					b.setCount(b.getCount() + 1); 
					
					cookie.setPath(req.getContextPath());
					cookie.setMaxAge(1 * 60 * 60); // 1시간만 유지 
					res.addCookie(cookie);
				}
			}
		}else { // 조회된 board가 null인 경우
			model.addAttribute("errorMsg" , "게시글 조회 실패 ....");
			return "common/errorPage";
		}
		
		model.addAttribute("board",b);
		
		return "board/boardDetailView";
	}
	
	// 일반게시판 수정하기기능
	// 첨부파일 / 이미지 수정하기.
	// + 첨부파일이나 이미지 수정하는 경우, 웹서버에 저장된 파일 신경쓰지 말기. db정보만 바꿔주기
	@GetMapping("/fileDownload/{boardNo}")
	public ResponseEntity<Resource> fileDownload(@PathVariable("boardNo") int boardNo) {
		
		ResponseEntity<Resource> responseEntity = null;
		// db에서 board테이블에서 boardNo값과 일치하는 행의 파일정보 조회
		BoardExt b = boardService.selectBoard(boardNo);
		
		if(b.getOriginName() == null) {
			return ResponseEntity.notFound().build(); // 응답상태 404로 설정후 반환
		}
		
		// Resource객체로 파일 얻어오기
		String saveDirectory = application.getRealPath("/resources/images/board/"+b.getBoardCd()+"/");
		File downFile = new File(saveDirectory , b.getChangeName());// 디렉토리 경로(saveDirectory)상에서
		// 두번째 매개변수로 전달받은 파일을 찾아서 객체 생성
		Resource resource = resourceLoader.getResource("file:"+downFile);
		try {
			String filename = new String(b.getOriginName().getBytes("utf-8") , "iso-8859-1");
			// utf-8방식으로 인코딩된 데이터(문자열)을 iso-8859-1로 변환 -> http통신할때 인코딩방식을 iso-8859-1로 해야하기 때문.
			
			responseEntity = ResponseEntity.ok()
					// 내가 넘겨주고자하는 데이터가 바이너리형식의 데이터임을 의미.
							.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
							//CONTENT_DISPOSITION : 파일을 첨부파일 형태로 처리하겠다는 의미(다운로드)
							.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+filename)
							.body(resource);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		
		return responseEntity;
	}
	
	@GetMapping("/update/{boardCode}/{boardNo}")
	public String updateBoard(
			@PathVariable(value = "boardCode") String boardCode,
			@PathVariable("boardNo") int boardNo,
			Model model
			) {
		// 게시글정보(selectBoard), 첨부파일정보 조회후 함께 전달.
		BoardExt board = boardService.selectBoard(boardNo);
		// 게시글내용(boardContent)은 개행처리된 상태이기 때문에 <br>태그를 /n로 변경해주는 작업이 필요.
		board.setBoardContent(Utils.newLineClear(board.getBoardContent()));
	
		//PathVariable로 자동으로 request에 등록됨
//		model.addAttribute("boardCode", boardCode);
//		model.addAttribute("boardNo", boardNo);
		
		model.addAttribute("board", board);
		
		return "board/boardUpdateForm";
	}
	
	@PostMapping("/update/{boardCode}/{boardNo}")
	public String updateBoard2(
			Board board, 
			@PathVariable("boardCode") String boardCode,
			@PathVariable("boardNo") int boardNo,
			// 리다이렉트할때 request스코프에 저장시킬 데이터를 담아주는 변수
			RedirectAttributes ra, 
			Model model ,
			// 첨부파일
			@RequestParam(value="upfile" , required = false ) MultipartFile upfile , 
			// 이미지파일들
			@RequestParam(value="upfiles" , required = false) List<MultipartFile> upfiles,
			String deleteList // [1,2,3]
			) {

		// 1) Board테이블 정보수정 => Update
		board.setBoardNo(boardNo);
		board.setBoardCd(boardCode);
		log.info("board ?? {} deleteList ?? {} " , board , deleteList); // boardNo, boardTitle, boardContent
		
		int result = 0;
		try {
			result = boardService.updateBoard(board , deleteList, upfile, upfiles);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 2) 이미지파일들 정보 수정 => UPDATE ,INSERT, DELETE
		// 사진이 없던곳에 새롭게 추가된경우 -> INSERT 
		// 사진이 있던곳에 새롭게 추가된경우 -> UPDATE
		// 사진이 있던곳에 삭제가 된경우    -> DELETE
		// 사진이 있거나,없던곳에 그대로 없는경우 -> X
		
		// 3) 작업결과에 따른 페이지 지정
		if(result > 0) {
			// board/list/C
			// board/update/C/6			
			//수정 성공시 list로 리다이렉트 되도록 설정(상대경로방식으로)
			ra.addFlashAttribute("alertMsg", "게시글 수정 성공 ㅎㅎ");
			return "redirect:../../list/"+boardCode;			
		}else {
			model.addAttribute("errorMsg", "게시글 수정 실패");
			return "common/errorPage";
		}
	}
	
	
	
	
	
	
	
}
