package com.example.borad.service;

import com.example.borad.entity.Board;
import com.example.borad.entity.Comment;
import com.example.borad.repository.BoardRepository;
import com.example.borad.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    public void save(Long boardId, String content, String writer) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다"));
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setWriter(writer);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setBoard(board);
        commentRepository.save(comment);
    }

    public void delete(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다"));
        if (!comment.getWriter().equals(username)) {
            throw new RuntimeException("삭제 권한이 없습니다");
        }
        commentRepository.deleteById(commentId);
    }

    public void update(Long commentId, String content, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다"));
        if (!comment.getWriter().equals(username)) {
            throw new RuntimeException("수정 권한이 없습니다");
        }
        comment.setContent(content);
        commentRepository.save(comment);
    }

    public List<Comment> findByBoardId(Long boardId) {
        return commentRepository.findByBoardIdOrderByCreatedAtAsc(boardId);
    }
}
