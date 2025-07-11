package com.kh.spring.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.chat.model.service.ChatService;
import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.chat.model.vo.ChatRoom;
import com.kh.spring.chat.model.vo.ChatRoomJoin;
import com.kh.spring.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/chat")
@Slf4j
@SessionAttributes({"chatRoomNo"})// model영역에 추가하는 key값이 chatRoomNo인 데이터를 HttpSession에 저장
public class ChatController {
	
	@Autowired
	private ChatService chatService;
	
	@GetMapping("/chatRoomList")
	public String selectChatRoomList(Model model) {
		/* 
		 * 업무로직
		 * 1. 채팅방목록 조회
		 * 2. 조회된 데이터를 담아 forward
		 * 3. 페이징처리(생략)
		 * */
		List<ChatRoom> list = chatService.selectChatRoomList();
		model.addAttribute("list",list);
		
		return "chat/chatRoomList";
	}
	
	@PostMapping("/openChatRoom")
	public String openChatRoom(
			@ModelAttribute ChatRoom room,
			RedirectAttributes ra,
			Authentication authentication
			) {
		/* 
		 * 1. 유효성검사(생략)
		 * 2. 테이블에 데이터 추가를 위해 room을 서비스로 전달
		 * 3. 처리결과에 따른 view페이지 지정
		 * */
		Member m = (Member) authentication.getPrincipal();
		room.setUserNo(m.getUserNo());
		
		int result = chatService.openChatRoom(room);
		
		if(result == 0) {
			throw new RuntimeException("채팅방 등록 실패");
		}
		
		ra.addFlashAttribute("alertMsg", "채팅방 생성 성공");
		return "redirect:/chat/chatRoomList";
	}
	
	// 채팅방 입장 == 게시글 상세보기
	@GetMapping("/room/{chatRoomNo}")
	public String joinChatRoom(
			@PathVariable("chatRoomNo") int chatRoomNo,
			Model model,
			ChatRoomJoin join,
			Authentication authentication			
			) {
		/* 
		 * 업무로직
		 * 1. 채팅방 번호를 기준으로 채팅방 메세지내용 조회
		 * 2. 참여자수 증가
		 * 3. 채팅방 메세지를 model에 추가후 forward
		 * */
		Member loginUser = (Member) authentication.getPrincipal();
		join.setChatRoomNo(chatRoomNo);
		join.setUserNo(loginUser.getUserNo());
		
		List<ChatMessage> list = chatService.joinChatRoom(join);
		
		if(list == null) throw new RuntimeException("채팅방 접속 오류");
		
		model.addAttribute("list",list);
		model.addAttribute("loginUser",loginUser);
		model.addAttribute("chatRoomNo",chatRoomNo); // session
		
		return "chat/chatRoom";
	}
	
	
	
	
}


















