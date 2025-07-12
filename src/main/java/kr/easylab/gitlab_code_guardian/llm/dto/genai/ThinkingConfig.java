package kr.easylab.gitlab_code_guardian.llm.dto.genai;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThinkingConfig {
    private Long thinkingBudget;
}
