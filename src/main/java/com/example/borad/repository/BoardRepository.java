package com.example.borad.repository;

import com.example.borad.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Board> findByWriterContainingIgnoreCase(String writer, Pageable pageable);
    Page<Board> findByTitleContainingIgnoreCaseOrWriterContainingIgnoreCase(String title, String writer, Pageable pageable);
}