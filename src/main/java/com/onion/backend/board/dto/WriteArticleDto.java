package com.onion.backend.board.dto;

import lombok.Getter;

@Getter
public class WriteArticleDto {

    private final Long boardId;
    private final String title;
    private final String content;
    private final String username;

    public WriteArticleDto(Long boardId, String title, String content, String username) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.username = username;
    }
}
