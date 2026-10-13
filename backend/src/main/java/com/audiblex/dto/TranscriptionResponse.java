package com.audiblex.dto;

import com.audiblex.entity.Transcription;
import java.time.LocalDateTime;

public record TranscriptionResponse(
        Long id,
        String title,
        String transcript,
        String audioFileName,
        LocalDateTime createdAt) {
    public static TranscriptionResponse from(Transcription transcription) {
        return new TranscriptionResponse(
                transcription.getId(),
                transcription.getTitle(),
                transcription.getTranscript(),
                transcription.getAudioFileName(),
                transcription.getCreatedAt());
    }
}
