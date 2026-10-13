package com.audiblex.repository;

import com.audiblex.entity.Transcription;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranscriptionRepository extends JpaRepository<Transcription, Long> {
    List<Transcription> findByTitleContainingIgnoreCaseOrTranscriptContainingIgnoreCaseOrderByCreatedAtDesc(
            String title, String transcript);

    List<Transcription> findAllByOrderByCreatedAtDesc();
}
