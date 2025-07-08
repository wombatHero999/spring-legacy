package com.kh.spring.common;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utils {

	// 파일 저장 함수
	// 파일을 저장시키면서, 파일명을 수정한 후 수정된 파일명을 반환
	public static String saveFile(MultipartFile upfile, ServletContext application,  String boardCode) {
		//이미지, 파일을 저장할 저장경로 얻어오기
		String webPath = "/resources/images/board/"+boardCode+"/";
		// getRealPath(경로)
		//  - 실제 서버의 파일 시스템 경로를 절대경로로 반환하는 메서드
		//  - 클라이언트가 업로드한 파일을 저장하기 위한 저장경로 반환		
		// 현재 pc기준 C:/springworkspace/resources/images/board/ ...
		String serverFolderPath = application.getRealPath(webPath);
		
		//System.out.println(serverFolderPath);
		
		//경로리가 존재하지 않는다면 생성해주는 코드 추가
		File dir = new File(serverFolderPath);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		// 랜덤파일명 생성하기
		String originName = upfile.getOriginalFilename();
		String currentTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
		int random = (int)(Math.random() * 90000 + 10000); // 10000~99999 5자리 랜덤값
		String ext = originName.substring(originName.indexOf(".")); // .
		
		String changeName = currentTime +  random  +  ext ;
		
		try {
			// 서버에 지정된 파일명으로 팡리을 저장하는 메서드
			upfile.transferTo(new File(serverFolderPath+changeName));
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		
		return changeName;
	}
	
	//크로스 사이트 스크립트 공격을 방지하기위한 메소드
	//  - 스크립트삽입공격
	//  - 사용자가 <script>태그를 게시글에 저장하여 클라이언트가 게시글 클릭시 자바스크립트가 실행
	//    되도록 유도하는 방식
	//  - 위 내용을 그대로 db에 저장 후 브라우저에 렌더링하면 문제가 발생할 수 있으므로
	//    태그가 아닌 기본 문자열로 인식할 수 있게끔 html내부 entity로 변환처리를 수행한다.
	//    ex) <는 &lt; >는 &gt;로
	public static String XSSHandling(String content) {
		if(content != null) {
			content = content.replaceAll("&", "&amp;");
			content = content.replaceAll("<", "&lt;");
			content = content.replaceAll(">", "&gt;");
			content = content.replaceAll("\"", "&quot;");
		}
		return content;
	}
	
	// 개행문자 처리
	// textarea -> \n , p -> <br>
	public static String newLineHandling(String content) {
		return content.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
	}
	
	// 개행해제 처리
	public static String newLineClear(String content) {
		return content.replaceAll("<br>","\n");
	}
	
	
	
}
