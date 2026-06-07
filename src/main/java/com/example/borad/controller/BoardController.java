package com.example.borad.controller;

import com.example.borad.entity.Board;
import com.example.borad.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;

    @GetMapping("/board")
    public String list(Model model, @RequestParam(defaultValue = "0") int page) {
        PageRequest pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<Board> boardList = boardRepository.findAll(pageable);
        model.addAttribute("boardList", boardList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", boardList.getTotalPages());
        return "board/list";
    }

    @GetMapping("/board/write")
    public String writeForm() {
        return "board/write";
    }

    @PostMapping("/board/write")
    public String write(Board board) {
        board.setCreatedAt(LocalDateTime.now());
        boardRepository.save(board);
        return "redirect:/board";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다"));
        model.addAttribute("board", board);
        return "board/detail";
    }

    @GetMapping("/board/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다"));
        model.addAttribute("board", board);
        return "board/edit";
    }

    @PostMapping("/board/edit/{id}")
    public String edit(@PathVariable Long id, Board board) {
        Board existingBoard = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다"));
        existingBoard.setTitle(board.getTitle());
        existingBoard.setWriter(board.getWriter());
        existingBoard.setContent(board.getContent());
        boardRepository.save(existingBoard);
        return "redirect:/board/" + id;
    }

    @GetMapping("/board/delete/{id}")
    public String delete(@PathVariable Long id) {
        boardRepository.deleteById(id);
        return "redirect:/board";
    }
}