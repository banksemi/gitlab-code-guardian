package kr.easylab.gitlab_code_guardian.review.service;

import kr.easylab.gitlab_code_guardian.llm.dto.LLMConfig;
import kr.easylab.gitlab_code_guardian.llm.dto.LLMMessage;
import kr.easylab.gitlab_code_guardian.llm.service.LLMService;
import kr.easylab.gitlab_code_guardian.prompt.system.service.SystemPromptService;
import kr.easylab.gitlab_code_guardian.prompt.user.service.UserPromptService;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MergeRequestReviewServiceImpl implements MergeRequestReviewService {
    private final ContentAggregator contentAggregator;
    private final LLMService llmService;
    private final UserPromptService userPromptService;
    private final SystemPromptService systemPromptService;

    public MRReview review() {
        String systemPrompt = systemPromptService.getPrompt();
        List<LLMMessage> messages = new ArrayList<>();

        String userPrompt = userPromptService.getPrompt();
        if (userPrompt != null && !userPrompt.isEmpty()) {
            messages.add(
                    LLMMessage.builder()
                            .role(LLMMessage.Role.USER)
                            .text(userPromptService.getPrompt())
                            .build()
            );
        }

        // 리뷰를 위한 MR 컨텍스트 추가
        messages.addAll(contentAggregator.aggregate());

        LLMConfig llmConfig = LLMConfig.builder().prompt(systemPrompt).build();
        log.info("LLM Config: {}", llmConfig);
        log.info("LLM Messages: {}", messages);
        return llmService.generate(messages, MRReview.class, llmConfig);
    }
}
