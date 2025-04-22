package me.hoyeon.shortlink.ui;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateShortUrlRequest(
    @NotBlank
    @Pattern(regexp = "^https?://.*", message = "URL은 http 또는 https로 시작해야 합니다")
    String originalUrl
) {

}
