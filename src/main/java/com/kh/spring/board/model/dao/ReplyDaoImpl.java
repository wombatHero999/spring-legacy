package com.kh.spring.board.model.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kh.spring.board.model.vo.Reply;

public class ReplyDaoImpl implements ReplyDao {

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
