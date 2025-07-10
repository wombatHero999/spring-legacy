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

@Slf4j
@Controller
@RequestMapping("/chat")
@SessionAttributes({"chatRoomNo"})
public class ChatController {
	@Autowired
	private ChatService chatService;
	
	@GetMapping("/chatRoomList")
	public String selectChatRoomList(Model model) {
		// 1) db에서 채팅방 목록데이터 조회.
		List<ChatRoom> list = chatService.selectChatRoomList();
		// 2) 조회된 데이터를 model안에 추가
		model.addAttribute("list",list);
		// 3) view페이지 포워딩
		
		return "chat/chatRoomList";
		
	}
	
	@PostMapping("/openChatRoom")
	public String openChatRoom(
			ChatRoom room,
			RedirectAttributes ra, 
			Authentication authentication) {
		Member loginUser = (Member) authentication.getPrincipal();
		room.setUserNo(loginUser.getUserNo());
		
		int chatRoomNo = chatService.openChatRoom(room); // 채팅방(chatRoom) 생성 및 생성된 채팅방 내부로 이동.
		String path ="redirect:/chat/";
		
		if(chatRoomNo > 0) {
			ra.addFlashAttribute("alertMsg","채팅방생성 성공");
			//path += "room/"+chatRoomNo;
			path += "chatRoomList";
		}
		else {
			ra.addFlashAttribute("alertMsg","채팅방생성 실패");
			path += "chatRoomList";
		}
		
		return path;
	}
	//채팅방 입장 -> 게시글 상세보기
	@GetMapping("/room/{chatRoomNo}")
	public String joinChatRoom(
			@PathVariable("chatRoomNo") int chatRoomNo ,
			Model model,
			RedirectAttributes ra,
			ChatRoomJoin join,
			Authentication authentication
		) {
		// CHAT_ROOM_JOIN안에 참여한 채팅방번호(chatRoomNo)와 현재 참여한 회원번호(userNo)를 담아서 INSERT(참여인원수 증가)
		Member loginUser = (Member) authentication.getPrincipal();
		join.setChatRoomNo(chatRoomNo);
		join.setUserNo(loginUser.getUserNo());
		
		log.debug("join : {}", join);
		
		//채팅내용 조회후 model에 담아줄 예정.
		List<ChatMessage> list = chatService.joinChatRoom(join); // 채팅방 참여(insert)후 , 해당 채팅방의 채팅메세지 조회(select)
		log.info("채팅내용 {}" ,list);
		
		if(list == null) throw new RuntimeException("채팅방이 존재하지 않습니다.");

		model.addAttribute("loginUser",loginUser);
		model.addAttribute("list", list);
		model.addAttribute("chatRoomNo", chatRoomNo); // session으로 이관
		return "chat/chatRoom";
	}
	
	@GetMapping("/chatRoom/{chatRoomNo}/exit")
	public String exitChatRoom(
			Authentication authentication,
			@PathVariable("chatRoomNo") int chatRoomNo ,
			ChatRoomJoin join ,
			RedirectAttributes ra
			) {
		// DELETE FROM CHAT_ROOM_JOIN WHERE CHAT_ROOM_NO =? AND USER_NO=?
		Member loginUser = (Member) authentication.getPrincipal();
		join.setChatRoomNo(chatRoomNo);
		join.setUserNo(loginUser.getUserNo());
		// 채팅방 나가기 -> 만약 채팅방에 마지막으로 나간 인원이 본인이라면
		// 채팅방 삭제처리. 
		chatService.exitChatRoom(join);
		
		ra.addFlashAttribute("alertMsg", "채팅방을 나갔습니다.");
		
		return "redirect:/chat/chatRoomList";
	}
}
