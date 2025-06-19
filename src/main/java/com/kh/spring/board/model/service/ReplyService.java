package com.kh.spring.board.model.service;

import java.util.List;

import com.kh.spring.board.model.vo.Reply;

public interface ReplyService {

	List<Reply> selectReplyList(int boardNo);

	int insertReply(Reply r);

	int deleteReply(int replyNo);

	int updateReply(Reply r);

}
