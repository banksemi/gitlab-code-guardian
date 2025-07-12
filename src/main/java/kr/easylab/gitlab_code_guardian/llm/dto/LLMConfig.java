package kr.easylab.gitlab_code_guardian.llm.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Optional;

@Data
@Builder
public class LLMConfig {
    public enum ThinkingMode {
        yes,
        no
    }
    private String prompt;
    private ThinkingMode thinking;
}
