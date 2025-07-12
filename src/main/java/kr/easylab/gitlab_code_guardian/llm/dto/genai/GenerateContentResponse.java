package kr.easylab.gitlab_code_guardian.llm.dto.genai;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class GenerateContentResponse {
    List<Candidate> candidates;
}
