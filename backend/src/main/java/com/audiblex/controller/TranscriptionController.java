package com.audiblex.controller;

import com.audiblex.dto.TranscriptionResponse;
import com.audiblex.dto.UpdateTranscriptionRequest;
import com.audiblex.service.TranscriptionService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/transcriptions")
public class TranscriptionController {
    private final TranscriptionService service;

    public TranscriptionController(TranscriptionService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TranscriptionResponse> create(@RequestParam("file") MultipartFile file) {
        TranscriptionResponse created = service.create(file);
        return ResponseEntity.created(URI.create("/api/transcriptions/" + created.id())).body(created);
    }

    @GetMapping
    public List<TranscriptionResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TranscriptionResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public TranscriptionResponse update(
            @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody UpdateTranscriptionRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<TranscriptionResponse> search(@RequestParam(required = false) String keyword) {
        return service.search(keyword);
    }
}
