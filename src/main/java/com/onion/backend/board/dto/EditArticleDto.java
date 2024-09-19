package com.onion.backend.board.dto;

import lombok.Getter;

import java.util.Optional;

@Getter
public class EditArticleDto {

    private final Long boardId;
    private final Optional<String> title;
    private final Optional<String> content;
    private final String username;

    public EditArticleDto(Long boardId, Optional<String> title, Optional<String> content, String username) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.username = username;
    }
}
