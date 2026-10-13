package com.audiblex.service;

import com.audiblex.exception.TranscriptionException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class OpenAiTranscriptionClient {
    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public OpenAiTranscriptionClient(
            RestClient.Builder builder,
            @Value("${openai.api-key:}") String apiKey,
            @Value("${openai.model:whisper-1}") String model) {
        this.restClient = builder.baseUrl("https://api.openai.com/v1").build();
        this.apiKey = apiKey;
        this.model = model;
    }

    public String transcribe(byte[] audio, String fileName) {
        if (apiKey.isBlank()) {
            throw new TranscriptionException("OpenAI is not configured. Set OPENAI_API_KEY on the backend.");
        }

        ByteArrayResource audioResource = new ByteArrayResource(audio) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", audioResource);
        form.add("model", model);
        form.add("response_format", "json");

        try {
            Map<?, ?> response = restClient.post()
                    .uri("/audio/transcriptions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(form)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, result) -> {
                        throw new TranscriptionException(
                                "OpenAI transcription failed (HTTP " + result.getStatusCode().value()
                                        + "). Check the API key, account access, and audio file.");
                    })
                    .body(Map.class);
            Object text = response == null ? null : response.get("text");
            if (!(text instanceof String transcript) || transcript.isBlank()) {
                throw new TranscriptionException("OpenAI returned an empty transcript.");
            }
            return transcript;
        } catch (TranscriptionException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new TranscriptionException("Could not reach the OpenAI transcription service.", exception);
        }
    }
}
