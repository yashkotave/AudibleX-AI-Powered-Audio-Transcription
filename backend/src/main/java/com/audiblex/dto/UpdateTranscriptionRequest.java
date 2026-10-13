package com.audiblex.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTranscriptionRequest(
        @NotBlank @Size(max = 255) String title,
        @NotBlank String transcript) {
}
