package com.onion.backend.board.dto;

import com.onion.backend.board.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailResponse {

    private Long articleId;
    private String title;
    private String content;

    private List<CommentResponse> comments;

    public static ArticleDetailResponse toResponse(Article article){
        return new ArticleDetailResponse(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getComments().stream().map((comment) -> new CommentResponse(comment.getId(), comment.getContent())).toList()
        );
    }
}
