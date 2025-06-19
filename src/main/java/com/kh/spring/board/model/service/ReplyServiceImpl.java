package com.kh.spring.board.model.service;

import java.util.List;

import com.kh.spring.board.model.dao.ReplyDao;
import com.kh.spring.board.model.vo.Reply;

public class ReplyServiceImpl implements ReplyService {

	private ReplyDao replyDao;

	@Override
	public List<Reply> selectReplyList(int boardNo) {

		return null;
	}

	@Override
	public int insertReply(Reply r) {
		return 0;
	}

	@Override
	public int deleteReply(int replyNo) {

		return 0;
	}

	@Override
	public int updateReply(Reply r) {

		return 0;
	}

}
