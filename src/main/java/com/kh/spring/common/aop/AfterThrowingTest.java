package com.kh.spring.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Aspect
@Slf4j
public class AfterThrowingTest {

	
	@AfterThrowing(pointcut = "CommonPointcut.commonPoint()", throwing="execeptionObj")
	public void returnException(JoinPoint jp, Exception execeptionObj) {
		StringBuilder sb = new StringBuilder("Exception : "+execeptionObj.getStackTrace()[0]);
		sb.append("에러 메세지 : "+execeptionObj.getMessage()+"\n");
		log.error(sb.toString());
	}
	
}







