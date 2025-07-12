package kr.easylab.gitlab_code_guardian.prompt.system.service;

import org.springframework.stereotype.Service;

@Service
public class DefaultSystemPromptService implements SystemPromptService {
    @Override
    public String getPrompt() {
        return "당신은 AI 리뷰어입니다.";
    }
}
