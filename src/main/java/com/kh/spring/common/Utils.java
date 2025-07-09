package com.kh.spring.common;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;

import org.springframework.web.multipart.MultipartFile;

public class Utils {
	
	// 파일저장함수
	// 파일을 저장하면서, 파일명을 수정하고 수정된 파일명을 반환
	public static String saveFile(MultipartFile upfile, ServletContext application, String boardCode) {
		// 첨부파일을 저장할 저장경로 획득
		String webPath = "/resources/images/board/"+boardCode+"/";
		// getRealPath(경로)
		//  - 실제 서버의 파일 시스템 경로를 절대경로로 반환하는 메서드
		// ex) C:/springWorkspace/spring-project/....
		String serverFolderPath = application.getRealPath(webPath);
		System.out.println(serverFolderPath);
		// 저장경로가 존재하지 않는나면 생성
		File dir = new File(serverFolderPath);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		// 랜덤한 파일명 생성
		String originName = upfile.getOriginalFilename(); // 파일의 원본명
		String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		int random = (int)(Math.random() * 90000 + 10000); // 5자리 랜덤값
		String ext = originName.substring(originName.lastIndexOf(".")); 
		String changeName = currentTime + random+ext;
		// 서버에 파일을 업로드
		try {
			upfile.transferTo(new File(serverFolderPath+changeName));
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 파일명 반환
		return webPath+changeName;
	}
	
	/*
	 *  XSS(크로스 사이트 스크립트)공격을 방지하기 위한 메서드
	 *   - 스크립트 삽입 공격
	 *   - 사용자가 <script>태그를 게시글에 작성하여 클라이언트가 게시글 클릭시
	 *     script에 지정한 코드가 실행되도록 유도하는 방식
	 *   - 위 내용을 그대로 db에 저장 후 브라우저에 렌더링하면 문제가 발생할 수 잇
	 *     으므로 태그가 아닌 기본 문자열로 인식할 수 있게끔 html내부 entitiy로 변환
	 *     처리를 수행한다.
	 *  */	
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











