package com.onion.backend.board.dto;

import lombok.Getter;

import java.util.Optional;

@Getter
public class EditArticleRequest {

    private Optional<String> title;
    private Optional<String> content;
}
