package com.audiblex.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "transcriptions")
public class Transcription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "transcript", nullable = false, columnDefinition = "LONGTEXT")
    private String transcript;

    @Column(name = "audioFileName", nullable = false, length = 255)
    private String audioFileName;

    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Transcription() {
    }

    public Transcription(String title, String transcript, String audioFileName) {
        this.title = title;
        this.transcript = transcript;
        this.audioFileName = audioFileName;
    }

    @PrePersist
    void setCreatedAt() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public String getAudioFileName() {
        return audioFileName;
    }

    public void setAudioFileName(String audioFileName) {
        this.audioFileName = audioFileName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
