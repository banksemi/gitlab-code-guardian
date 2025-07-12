package kr.easylab.gitlab_code_guardian.llm.dto.genai;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class GenerateContentRequest {
    private SystemInstruction systemInstruction;
    private List<Content> contents;
    private GenerationConfig generationConfig;
}
