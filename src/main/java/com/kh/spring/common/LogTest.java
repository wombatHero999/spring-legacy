package com.kh.spring.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogTest {
		
	/*
	 * Logging level
	 * 
	 * - fatal
	 *     - 치명적 에러를 의미.(현재버전에서는 존재하지 않는 레벨)     
	 * - error
	 *     - 요청 처리중 발생하는 오류에 사용하는 메서드
	 * - warn
	 *     - 경고성 메세지 작성시 사용하는 메서드.
	 * - info
	 *     - 요청처리중 발생하는 정보성 메세지 출력시 사용하는 메서드
	 * - debug
	 *     - 개발중에 필요한 정보성 메세지 출력시 사용.
	 * - trace
	 *     - 가장 상세한 로깅 레벨로 디버그보다 많은 내부 정보를 출력해준다.
	 *  */
	public static void main(String[] args) {
		log.error("error - {}", "에러메세지");
		log.warn("warn - {}" , "경고메세지");
		log.info("info - {}","인포메세지");
		log.debug("debug - {}","디버그");
		log.trace("trace - {}","트레이스");
	}

}







