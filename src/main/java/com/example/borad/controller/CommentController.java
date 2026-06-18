package com.example.borad.controller;

import com.example.borad.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment/write")
    public String write(@RequestParam Long boardId,
                        @RequestParam String content,
                        @AuthenticationPrincipal UserDetails userDetails) {
        commentService.save(boardId, content, userDetails.getUsername());
        return "redirect:/board/" + boardId;
    }

    @PostMapping("/comment/update/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam Long boardId,
                         @RequestParam String content,
                         @AuthenticationPrincipal UserDetails userDetails) {
        commentService.update(id, content, userDetails.getUsername());
        return "redirect:/board/" + boardId;
    }

    @GetMapping("/comment/delete/{id}")
    public String delete(@PathVariable Long id,
                         @RequestParam Long boardId,
                         @AuthenticationPrincipal UserDetails userDetails) {
        commentService.delete(id, userDetails.getUsername());
        return "redirect:/board/" + boardId;
    }
}
