package com.onion.backend.board.dto;

import lombok.Getter;

import java.util.Optional;

@Getter
public class EditCommentDto {

    private final Long articleId;
    private final String content;

    public EditCommentDto(Long articleId, String content) {
        this.articleId = articleId;
        this.content = content;
    }
}
