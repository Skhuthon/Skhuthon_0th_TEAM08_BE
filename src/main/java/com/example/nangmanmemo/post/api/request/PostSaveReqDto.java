package com.example.nangmanmemo.post.api.request;

import java.time.LocalDateTime;

public record PostSaveReqDto(
        String title,
        String content,
        LocalDateTime postDate
) {
}