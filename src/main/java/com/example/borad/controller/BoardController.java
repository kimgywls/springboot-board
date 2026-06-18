package com.example.borad.controller;

import com.example.borad.entity.Board;
import com.example.borad.repository.BoardRepository;
import com.example.borad.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;
    private final CommentService commentService;

    @Value("${file.upload.path}")
    private String uploadPath;

    private static final List<String> ALLOWED_EXT = List.of("jpg", "jpeg", "png", "gif");

    @GetMapping("/board")
    public String list(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "all") String searchType) {
        PageRequest pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        Page<Board> boardList;

        if (keyword.isBlank()) {
            boardList = boardRepository.findAll(pageable);
        } else {
            boardList = switch (searchType) {
                case "title"  -> boardRepository.findByTitleContainingIgnoreCase(keyword, pageable);
                case "writer" -> boardRepository.findByWriterContainingIgnoreCase(keyword, pageable);
                default       -> boardRepository.findByTitleContainingIgnoreCaseOrWriterContainingIgnoreCase(keyword, keyword, pageable);
            };
        }

        model.addAttribute("boardList", boardList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", boardList.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
        return "board/list";
    }

    @GetMapping("/board/write")
    public String writeForm() {
        return "board/write";
    }

    @PostMapping("/board/write")
    public String write(Board board,
                        @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            String savedName = saveFile(file);
            if (savedName == null) return "redirect:/board/write?error=invalidType";
            board.setFileName(file.getOriginalFilename());
            board.setFilePath("/uploads/" + savedName);
        }
        board.setCreatedAt(LocalDateTime.now());
        boardRepository.save(board);
        return "redirect:/board";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다"));
        model.addAttribute("board", board);
        model.addAttribute("comments", commentService.findByBoardId(id));
        return "board/detail";
    }

    @GetMapping("/board/edit/{id}")
    public String editForm(@PathVariable Long id, Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다"));
        if (!board.getWriter().equals(userDetails.getUsername())) {
            return "redirect:/board/" + id + "?error=forbidden";
        }
        model.addAttribute("board", board);
        return "board/edit";
    }

    @PostMapping("/board/edit/{id}")
    public String edit(@PathVariable Long id, Board board,
                       @RequestParam(value = "file", required = false) MultipartFile file,
                       @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Board existingBoard = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다"));
        if (!existingBoard.getWriter().equals(userDetails.getUsername())) {
            return "redirect:/board/" + id + "?error=forbidden";
        }
        existingBoard.setTitle(board.getTitle());
        existingBoard.setWriter(board.getWriter());
        existingBoard.setContent(board.getContent());
        if (file != null && !file.isEmpty()) {
            String savedName = saveFile(file);
            if (savedName != null) {
                existingBoard.setFileName(file.getOriginalFilename());
                existingBoard.setFilePath("/uploads/" + savedName);
            }
        }
        boardRepository.save(existingBoard);
        return "redirect:/board/" + id;
    }

    @GetMapping("/board/delete/{id}")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails userDetails) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다"));
        if (!board.getWriter().equals(userDetails.getUsername())) {
            return "redirect:/board/" + id + "?error=forbidden";
        }
        boardRepository.deleteById(id);
        return "redirect:/board";
    }

    private String saveFile(MultipartFile file) throws IOException {
        String original = Objects.requireNonNull(file.getOriginalFilename());
        String ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) return null;
        String saved = UUID.randomUUID() + "_" + original;
        Path path = Paths.get(uploadPath + saved);
        Files.createDirectories(path.getParent());
        file.transferTo(path.toFile());
        return saved;
    }
}
