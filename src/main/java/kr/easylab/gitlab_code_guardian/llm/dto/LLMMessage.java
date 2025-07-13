package kr.easylab.gitlab_code_guardian.llm.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
public class LLMMessage {
    public enum Role {
        USER, ASSISTANT
    }
    private Role role;
    private String text;
}
