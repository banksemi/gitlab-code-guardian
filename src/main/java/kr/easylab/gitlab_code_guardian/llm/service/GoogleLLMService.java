package kr.easylab.gitlab_code_guardian.llm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.media.Schema;
import kr.easylab.gitlab_code_guardian.llm.dto.LLMConfig;
import kr.easylab.gitlab_code_guardian.llm.dto.LLMMessage;
import kr.easylab.gitlab_code_guardian.llm.dto.genai.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GoogleLLMService implements LLMService {
    private final String baseURL;
    private final String apiKey;
    private final String model;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final GoogleSchemaMappingService googleSchemaMappingService;
    private final SchemaExtractionService schemaExtractionService;

    public GoogleLLMService(
            @Value("${llm.google.base_url}") String baseURL,
            @Value("${llm.google.api_key}") String apiKey,
            @Value("${llm.google.model}") String model,
            ObjectMapper objectMapper,
            GoogleSchemaMappingService googleSchemaMappingService,
            SchemaExtractionService schemaExtractionService
    ) {
        this.baseURL = baseURL;
        this.apiKey = apiKey;
        this.model = model;
        this.objectMapper = objectMapper;
        this.googleSchemaMappingService = googleSchemaMappingService;
        this.schemaExtractionService = schemaExtractionService;

        this.webClient = WebClient.builder()
                .baseUrl(baseURL)
                .build();
    }

    private String mapRole(LLMMessage.Role role) {
        switch (role) {
            case USER:
                return "user";
            case ASSISTANT:
                return "model";
            default:
                throw new IllegalArgumentException("invalid role");
        }
    }

    private String call(LLMConfig llmConfig, List<LLMMessage> messages, GenerationConfig config) {
        if (llmConfig.getThinkingBudget() != null)
            config.setThinkingConfig(
                    ThinkingConfig.builder()
                            .thinkingBudget(
                                    llmConfig.getThinkingBudget()
                            ).build()
            );
        config.setTemperature(0.1f);
        List<Content> contents = messages.stream().map(
                message -> {
                    return Content.builder()
                            .role(mapRole(message.getRole()))
                            .parts(List.of(new Part(message.getText())))
                            .build();
                }
        ).toList(); // 수정 불가능

        GenerateContentRequest request = GenerateContentRequest.builder()
                .systemInstruction(new SystemInstruction(llmConfig.getPrompt()))
                .contents(contents)
                .generationConfig(config)
                .build();

        Mono<GenerateContentResponse> stringMono = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/" + model + ":generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> {
                            log.error("Google AI Error (status_code): {}", clientResponse.statusCode().value());
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> {
                                        log.error("Google AI Error (response): {}", errorBody);
                                        return Mono.error(new RuntimeException("Google AI Error"));
                                    });
                        })

                .bodyToMono(GenerateContentResponse.class);

        GenerateContentResponse responseString = stringMono
                .timeout(Duration.ofMinutes(10))
                .block();

        Content responseContent = responseString.getCandidates().get(0).getContent();
        for (Part part : responseContent.getParts()) {
            if (part.getText() != null) {
                log.info("API Response:\n{}", part.getText());
                return part.getText();
            }
        }
        return null;
    }

    @Override
    public <T> T generate(List<LLMMessage> messages, Class<T> clazz, LLMConfig config) {
        ResolvedSchema resolvedSchema = schemaExtractionService.extractSchema(clazz);
        Map<String, Object> stringObjectMap = googleSchemaMappingService.mapToGoogleSchema(resolvedSchema.schema, resolvedSchema.referencedSchemas);
        String responseText = call(
                config,
                messages,
                GenerationConfig.builder()
                        .responseMimeType("application/json")
                        .responseSchema(stringObjectMap)
                        .build()
        );

        try {
            return objectMapper.readValue(responseText, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
