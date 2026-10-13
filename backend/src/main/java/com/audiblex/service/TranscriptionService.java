package com.audiblex.service;

import com.audiblex.dto.TranscriptionResponse;
import com.audiblex.dto.UpdateTranscriptionRequest;
import com.audiblex.entity.Transcription;
import com.audiblex.exception.ResourceNotFoundException;
import com.audiblex.repository.TranscriptionRepository;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TranscriptionService {
    private static final long MAX_FILE_SIZE = 25L * 1024 * 1024;
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
            "mp3", "mp4", "mpeg", "mpga", "m4a", "wav", "webm");

    private final TranscriptionRepository repository;
    private final OpenAiTranscriptionClient openAiClient;

    public TranscriptionService(TranscriptionRepository repository, OpenAiTranscriptionClient openAiClient) {
        this.repository = repository;
        this.openAiClient = openAiClient;
    }

    @Transactional
    public TranscriptionResponse create(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Choose a non-empty audio file.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Audio upload exceeds the 25 MB limit.");
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank() || !isSupported(originalName)) {
            throw new IllegalArgumentException("Unsupported audio format. Use MP3, MP4, MPEG, MPGA, M4A, WAV, or WEBM.");
        }
        try {
            String transcript = openAiClient.transcribe(file.getBytes(), originalName);
            Transcription saved = repository.save(new Transcription(originalName, transcript, originalName));
            return TranscriptionResponse.from(saved);
        } catch (IOException exception) {
            throw new IllegalArgumentException("The uploaded audio file could not be read.", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<TranscriptionResponse> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream().map(TranscriptionResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TranscriptionResponse findById(Long id) {
        return TranscriptionResponse.from(findEntity(id));
    }

    @Transactional
    public TranscriptionResponse update(Long id, UpdateTranscriptionRequest request) {
        Transcription transcription = findEntity(id);
        transcription.setTitle(request.title().trim());
        transcription.setTranscript(request.transcript().trim());
        return TranscriptionResponse.from(repository.save(transcription));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findEntity(id));
    }

    @Transactional(readOnly = true)
    public List<TranscriptionResponse> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        String query = keyword.trim();
        return repository
                .findByTitleContainingIgnoreCaseOrTranscriptContainingIgnoreCaseOrderByCreatedAtDesc(query, query)
                .stream().map(TranscriptionResponse::from).toList();
    }

    private Transcription findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transcription " + id + " was not found."));
    }

    private boolean isSupported(String fileName) {
        int extensionSeparator = fileName.lastIndexOf('.');
        if (extensionSeparator < 0 || extensionSeparator == fileName.length() - 1) {
            return false;
        }
        return SUPPORTED_EXTENSIONS.contains(fileName.substring(extensionSeparator + 1).toLowerCase(Locale.ROOT));
    }
}
