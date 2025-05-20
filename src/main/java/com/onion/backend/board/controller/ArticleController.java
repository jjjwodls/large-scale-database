package com.onion.backend.board.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.onion.backend.board.domain.Article;
import com.onion.backend.board.dto.*;
import com.onion.backend.board.service.ArticleService;
import com.onion.backend.board.service.ElasticSearchArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;



    @PostMapping("/{boardId}/articles")
    public ResponseEntity<Article> writeArticle(@PathVariable Long boardId, @RequestBody WriteArticleRequest writeArticleRequest) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        WriteArticleDto writeArticleDto = new WriteArticleDto(boardId, writeArticleRequest.getTitle(), writeArticleRequest.getContent(), userDetails.getUsername());
        return ResponseEntity.ok(articleService.writeArticle(writeArticleDto));
    }

    @GetMapping("/{boardId}/articles")
    public ResponseEntity<List<ArticleResponse>> getArticle(@PathVariable Long boardId
            , @RequestParam(required = false) Long lastId, @RequestParam(required = false) Long firstId) {

        if (lastId != null) {
            return ResponseEntity.ok(articleService.getOldArticle(boardId, lastId));
        }

        if (firstId != null) {
            return ResponseEntity.ok(articleService.getNewArticle(boardId, firstId));
        }

        return ResponseEntity.ok(articleService.firstGetArticle(boardId));
    }

    @PutMapping("/{boardId}/articles/{articleId}")
    public ResponseEntity<ArticleResponse> editArticle(@PathVariable Long boardId,@PathVariable Long articleId, @RequestBody EditArticleRequest editArticleRequest) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        EditArticleDto editArticleDto = new EditArticleDto(boardId, editArticleRequest.getTitle(), editArticleRequest.getContent(), userDetails.getUsername());
        return ResponseEntity.ok(articleService.editArticle(editArticleDto, articleId));
    }

    @DeleteMapping("/articles/{articleId}")
    public ResponseEntity<Long> deleteArticle(@PathVariable Long articleId){
        return ResponseEntity.ok(articleService.deleteArticle(articleId));
    }

    @GetMapping("/{boardId}/articles/{articleId}")
    public ResponseEntity<ArticleResponse> getArticle(@PathVariable Long articleId, @PathVariable Long boardId){
        return ResponseEntity.ok(
                ArticleResponse.from(articleService.getArticleByIdAndBoardId(articleId, boardId)));
    }
}
