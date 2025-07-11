package com.kh.spring.common.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduleController {
	
	/* 
	 * 1. 고정시간 간격으로 스케쥴링
	 * fixedDelay : 이전 작업 종료 시점으로 일정시간 지연 후 실행
	 * fixedRate : 이전 작업 시작 시점을 기준으로 일정간격으로 메서드 수행
	 * */
	//@Scheduled(fixedDelay = 5000) // 작업 종료 후 5초의 지연시간을 둔뒤 다시 실행
	public void fixedDelayTask() {
		log.debug("[fixedDelay] 작업 실행 됨 - {}",System.currentTimeMillis());
	}
	
	//@Scheduled(fixedRate = 5000)
	public void fixedRateTask() {
		log.debug("[fixedRate] 작업 실행 됨 - {}",System.currentTimeMillis());
	}
	
	/* 
	 * 2. cron 표현식
	 * 
	 * cron : 초 분 시 일 월 요일
	 *        * * * * * *
	 *    * : 모든 값 (매분, 매초, 매시간,..)
	 *    ? : 일,요일에서만 사용 가능(일자리에 ?에 들어가는 경우 일수는 신경쓰지 않는다)
	 *    - : 값의 범위(1-10)
	 *    , : 여러값을 지정(1,5,10)
	 *    / : 증가 단위(0/2 -> 0초부터 2초간격으로)
	 *    L : 마지막(매월 말일을 지정할 떄 사용)
	 *    W : 가장 가까운 평일(15W)
	 *    # : N번째 요일
	 *    
	 *    매일 오전 1시에 어떤 작업을 실행하게 하고 싶다
	 *    0 0 1 * * *
	 *    
	 *    매 시간 30분
	 *    0 30 * * * *
	 * */
	//@Scheduled(cron = "0 * * * * *") // 매 분 0초마다 작업을 수행
	public void testCron() {
		log.debug("크론 표현식 작업 실행 {} ", System.currentTimeMillis());
	}
	
}











