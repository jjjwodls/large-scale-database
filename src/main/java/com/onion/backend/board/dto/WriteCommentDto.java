package com.onion.backend.board.dto;

import lombok.Getter;

@Getter
public class WriteCommentDto {

    private final Long boardId;
    private final Long articleId;
    private final String content;

    public WriteCommentDto(Long boardId, Long articleId, String content) {
        this.boardId = boardId;
        this.articleId = articleId;
        this.content = content;
    }
}
