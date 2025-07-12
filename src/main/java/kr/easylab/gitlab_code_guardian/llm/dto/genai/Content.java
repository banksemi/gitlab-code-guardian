package kr.easylab.gitlab_code_guardian.llm.dto.genai;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Builder
@Getter
public class Content {
    private String role;
    private List<Part> parts;
}
