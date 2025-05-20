package com.onion.backend.board.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.onion.backend.board.dto.*;
import com.onion.backend.board.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/boards/{boardId}/articles/{articleId}/")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @PostMapping("comments")
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

    @GetMapping("comments")
    public ResponseEntity<ArticleDetailResponse> articleDetail(@PathVariable Long boardId,@PathVariable Long articleId) throws JsonProcessingException {
        return ResponseEntity.ok(commentService.getArticle(boardId, articleId));
    }

    @PutMapping("comments/{commentId}")
    public ResponseEntity<CommentResponse> editComment(@PathVariable Long articleId,@PathVariable Long commentId, @RequestBody EditCommentRequest editCommentRequest) {
        EditCommentDto editCommentDto = new EditCommentDto(articleId, editCommentRequest.getContent());
        return ResponseEntity.ok(commentService.editComment(editCommentDto, commentId));
    }

    @DeleteMapping("comments/{commentId}")
    public ResponseEntity<Long> deleteArticle(@PathVariable Long commentId){
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }
}
