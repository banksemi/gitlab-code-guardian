package kr.easylab.gitlab_code_guardian.review.service;

import kr.easylab.gitlab_code_guardian.llm.service.LLMService;
import kr.easylab.gitlab_code_guardian.prompt.system.service.SystemPromptService;
import kr.easylab.gitlab_code_guardian.prompt.user.service.UserPromptService;
import kr.easylab.gitlab_code_guardian.provider.content.service.ContentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Reviewer {
    private final List<ContentProvider> contentProviders;
    private final LLMService llmService;
    private final UserPromptService userPromptService;
    private final SystemPromptService systemPromptService;
}
