package com.audiblex.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.audiblex.entity.Transcription;
import com.audiblex.exception.TranscriptionException;
import com.audiblex.repository.TranscriptionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class TranscriptionServiceTest {
    @Mock
    private TranscriptionRepository repository;

    @Mock
    private OpenAiTranscriptionClient openAiClient;

    @InjectMocks
    private TranscriptionService service;

    @Test
    void rejectsUnsupportedAudioBeforeCallingOpenAi() {
        MultipartFile file = new MockMultipartFile(
                "file", "notes.txt", "text/plain", "not audio".getBytes());

        assertThrows(IllegalArgumentException.class, () -> service.create(file));
        verifyNoInteractions(openAiClient, repository);
    }

    @Test
    void storesOnlyTheTranscriptReturnedByOpenAi() {
        MultipartFile file = new MockMultipartFile(
                "file", "meeting.wav", "audio/wav", new byte[] { 1, 2, 3 });
        when(openAiClient.transcribe(any(byte[].class), any(String.class))).thenReturn("Actual provider transcript");
        when(repository.save(any(Transcription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(file);

        assertEquals("Actual provider transcript", response.transcript());
        assertEquals("meeting.wav", response.title());
        assertEquals("meeting.wav", response.audioFileName());
    }

    @Test
    void doesNotSendRequestsWithoutAnOpenAiKey() {
        OpenAiTranscriptionClient client = new OpenAiTranscriptionClient(
                org.springframework.web.client.RestClient.builder(), "", "whisper-1");

        assertThrows(TranscriptionException.class, () -> client.transcribe(new byte[] { 1 }, "audio.wav"));
    }
}
