package com.kh.spring.common;

import lombok.extern.slf4j.Slf4j;

// log4j를 사용하는 추상 라이브러리.
@Slf4j
public class LogTest {
	
	/* 
	 * Logging Level
	 * 
	 * - fatal 
	 *    - 치명적 에러를 의미하며, 현재 버전에서는 존재하지 않는 레벨.
	 *    - 치명적 오류는 error로 처리.
	 * - error : 요청 처리중 발생하는 오류에 사용
	 * - warn : 경고성 메세지. 실행에는 문제없지만, 향후 오류가 발생할
	 * 경우가 있을 경우 사용
	 * - info  : 요청처리중 발생하는 정보성메세지 출력시 사용
	 * - debug : 개발중에 필요한 로그가 있을 경우 사용
	 * - trace : 가장 상세한 로깅 레벨로, 디버그보다 더 많은 내부 정보를 출력
	 * */
	public static void main(String[] args) {
		log.error("error - {} " , "에레메시지임");
		log.warn("warn - {}" , "경고메세지");
		log.info("info - {}" , "인포메세지");
		log.debug("debug - {}" , "디버그");		
		log.trace("trace - {} ","트레이스");
	}

}
