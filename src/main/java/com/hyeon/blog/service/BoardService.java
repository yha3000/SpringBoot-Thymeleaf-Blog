package com.hyeon.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hyeon.blog.dto.ReplySaveRequestDto;
import com.hyeon.blog.model.Board;
import com.hyeon.blog.model.User;
import com.hyeon.blog.repository.BoardRepository;
import com.hyeon.blog.repository.ReplyRepository;

@Service
public class BoardService {

//	@Autowired
//	private UserRepository userRepository;
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private ReplyRepository replyRepository;
	
	@Transactional
	public void save(Board board, User user) { // title, content, username
		board.setCount(0);
		board.setUser(user);
		boardRepository.save(board);
	}
	
	@Transactional(readOnly = true)
	public Page<Board> list(Pageable pageable) {
		return boardRepository.findAll(pageable);
	}
	
	@Transactional(readOnly = true)
	public Board detail(int id) {
		return boardRepository.findById(id).orElseThrow(()->{
			return new IllegalArgumentException("글 상세보기 실패 : 아이디를 찾을 수 없습니다.");
		});
	}
	
	@Transactional
	public void delete(int id) {
		boardRepository.deleteById(id);
	}
	
	@Transactional
	public void update(int id, Board requestBoard) {
		Board board = boardRepository.findById(id).orElseThrow(()->{
			return new IllegalArgumentException("글 찾기 실패 : 아이디를 찾을 수 없습니다.");
		}); // 영속화 완료
		board.setTitle(requestBoard.getTitle());
		board.setContent(requestBoard.getContent());
		// 해당 함수로 종료시 (Service가 종료될 때) 트랜젝션이 종료됩니다
		// 이때 더티체킹 - 자동 업데이트가 됨. DB flush
	}

//	@Transactional
//	public void replySave(User user, int boardId, Reply requestReply) {
//		Board board = boardRepository.findById(boardId).orElseThrow(()->{
//			return new IllegalArgumentException("댓글 쓰기 실패 : 게시글 id를 찾을 수 없습니다.");
//		});
//		requestReply.setUser(user);
//		requestReply.setBoard(board);
//		replyRepository.save(requestReply);
//	}
	
	@Transactional
	public void replySave(ReplySaveRequestDto replySaveRequestDto) {
//		User user = userRepository.findById(replySaveRequestDto.getUserId()).orElseThrow(()->{
//			return new IllegalArgumentException("댓글 쓰기 실패 : 유저 id를 찾을 수 없습니다.");
//		});
//		Board board = boardRepository.findById(replySaveRequestDto.getBoardId()).orElseThrow(()->{
//			return new IllegalArgumentException("댓글 쓰기 실패 : 게시글 id를 찾을 수 없습니다.");
//		});

		// Builder 사용
//		Reply reply = Reply.builder()
//									.user(user)
//									.board(board)
//									.content(replySaveRequestDto.getContent())
//									.build();
		
		// update 메소드 생성 후 사용
//		Reply reply = new Reply();
//		reply.update(user, board, replySaveRequestDto.getContent());
		
//		replyRepository.save(reply);
		
		// nativeQuery 사용
		replyRepository.mSave(replySaveRequestDto.getUserId(), replySaveRequestDto.getBoardId(), replySaveRequestDto.getContent());
	}
	
	@Transactional
	public void replyDelete(int replyId) {
		replyRepository.deleteById(replyId);
	}
	
}
