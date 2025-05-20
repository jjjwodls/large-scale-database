package com.onion.backend.board.dto;

import com.onion.backend.board.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleResponse {

    private Long id;
    private String title;
    private String content;

    public static ArticleResponse from(Article article){
        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .build();
    }
}
