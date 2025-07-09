package com.kh.spring.common.scheduling;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ScheduleTask {
    /**
     * 1. 고정 시간 간격으로 실행하는 방식
     * 
     * fixedDelay: 이전 작업 종료 시점 기준으로 일정 지연 후 실행
     * fixedRate: 이전 작업 시작 시점 기준으로 일정 간격으로 실행
     */
    //@Scheduled(fixedDelay = 5000) // 이전 작업 종료 후 5초 뒤 실행
    public void fixedDelayTask() {
        log.info("[FixedDelay] 작업 실행됨 - {}", System.currentTimeMillis());
    }

    //@Scheduled(fixedRate = 5000) // 이전 작업 시작 후 5초 간격으로 실행
    public void fixedRateTask() {
        log.info("[FixedRate] 작업 실행됨 - {}", System.currentTimeMillis());
    }
	 /**
     * 2. cron 표현식 기반 실행
     * 
     * cron: 초 분 시 일 월 요일 (년도 생략 가능)
     * 예: 매일 오전 9시 30분에 실행 → "0 30 9 * * *"
     	* * * * * * 
		초 분 시 일 월 요일 
		초(0~59)  
		분(0~59)  
		시(0~23)  
		일(1~31)  
		월(1~12 or JAN–DEC)  
		요일(0~7 or SUN–SAT) [0,7=일요일]
	*   * : 모든 값 (ex. 매분, 매시간 등)
	*   ? : 일 또는 요일 자리에서만 사용 (둘 중 하나 생략 시) -> 일은 신경쓰지 않거나 , 요일을 신경쓰지 않을때
	*   - : 범위 지정 (예: 1-5)
	*   , : 여러 값 지정 (예: 1,3,5)
	*   / : 증가 단위 (예: 0/2 → 0부터 2씩 증가)
	*   L : 마지막 (월=말일, 요일=마지막 해당 요일)
	*   W : 가장 가까운 평일 (예: 15W → 15일 기준 평일)
	*   # : N번째 요일 (예: 6#3 → 세 번째 금요일)
		
		매시간 30분마다 특정 작업을 실행시키고 싶다.
		0 30 * * * * 
		
		매일 오전 1시에 실행하게하고싶다?
		0 0 1 * * * 
     * 
     */
    //@Scheduled(cron = "0 * * * * *") // 매 분 0초마다 실행
	public void testCron() {
		log.info("크론탭 방식 테스트");
	}
}
