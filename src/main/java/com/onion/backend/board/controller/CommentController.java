package com.onion.backend.board.controller;

import com.onion.backend.board.domain.Article;
import com.onion.backend.board.dto.*;
import com.onion.backend.board.service.ArticleService;
import com.onion.backend.board.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards/{boardId}/articles/{articleId}")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @PostMapping("/comments")
    public ResponseEntity<CommentResponse> writeArticle(@PathVariable Long boardId,@PathVariable Long articleId, @RequestBody WriteCommentRequest writeCommentRequest) {
        WriteCommentDto writeCommentDto = new WriteCommentDto(boardId, articleId, writeCommentRequest.getContent());
        return ResponseEntity.ok(commentService.writeComment(writeCommentDto));
    }

//    @GetMapping("/{boardId}/articles")
//    public ResponseEntity<List<ArticleResponse>> getArticle(@PathVariable Long boardId
//            , @RequestParam(required = false) Long lastId, @RequestParam(required = false) Long firstId) {
//
//        if (lastId != null) {
//            return ResponseEntity.ok(articleService.getOldArticle(boardId, lastId));
//        }
//
//        if (firstId != null) {
//            return ResponseEntity.ok(articleService.getNewArticle(boardId, firstId));
//        }
//
//        return ResponseEntity.ok(articleService.firstGetArticle(boardId));
//    }

    @PutMapping("comments/{commentId}")
    public ResponseEntity<CommentResponse> editArticle(@PathVariable Long articleId,@PathVariable Long commentId, @RequestBody EditCommentRequest editCommentRequest) {
        EditCommentDto editCommentDto = new EditCommentDto(articleId, editCommentRequest.getContent());
        return ResponseEntity.ok(commentService.editComment(editCommentDto, commentId));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Long> deleteArticle(@PathVariable Long commentId){
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }
}
