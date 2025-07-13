package kr.easylab.gitlab_code_guardian.review.service;

import kr.easylab.gitlab_code_guardian.llm.dto.LLMConfig;
import kr.easylab.gitlab_code_guardian.llm.dto.LLMMessage;
import kr.easylab.gitlab_code_guardian.llm.service.LLMService;
import kr.easylab.gitlab_code_guardian.prompt.system.service.SystemPromptService;
import kr.easylab.gitlab_code_guardian.prompt.user.service.UserPromptService;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MergeRequestReviewServiceImplTest {
    @Mock
    private ContentAggregator contentAggregator;

    @Mock
    private UserPromptService userPromptService;

    @Mock
    private SystemPromptService systemPromptService;

    @Mock
    private MRReaderService mrReaderService;

    @Mock
    private LLMService llmService;

    @Test
    void review() {
        // Given
        MergeRequestReviewServiceImpl mergeRequestReviewService = new MergeRequestReviewServiceImpl(
                contentAggregator, llmService, userPromptService, systemPromptService
        );
        when(systemPromptService.getPrompt()).thenReturn("시스템 프롬프트");
        when(userPromptService.getPrompt(mrReaderService)).thenReturn("유저 프롬프트");
        when(contentAggregator.aggregate(mrReaderService)).thenReturn(new ArrayList<>(
                List.of(
                        LLMMessage.builder().role(LLMMessage.Role.USER).text("Content1").build()
                )
        ));
        MRReview response = new MRReview();
        when(llmService.generate(anyList(), eq(MRReview.class), any(LLMConfig.class))).thenReturn(response);

        // When
        MRReview review = mergeRequestReviewService.review(mrReaderService);

        // Then
        verify(llmService).generate(List.of(
                LLMMessage.builder().role(LLMMessage.Role.USER).text("유저 프롬프트").build(),
                LLMMessage.builder().role(LLMMessage.Role.USER).text("Content1").build()
        ), MRReview.class, LLMConfig.builder().prompt("시스템 프롬프트").build());
        assertEquals(response, review);
    }

    @Test
    @DisplayName("사용자 지정 프롬프트가 없을 때 잘 작동하는지 확인합니다.")
    void review_withEmptyUserPrompt() {
        // Given
        MergeRequestReviewServiceImpl mergeRequestReviewService = new MergeRequestReviewServiceImpl(
                contentAggregator, llmService, userPromptService, systemPromptService
        );
        when(systemPromptService.getPrompt()).thenReturn("시스템 프롬프트");
        when(userPromptService.getPrompt(mrReaderService)).thenReturn(""); // Empty user prompt
        when(contentAggregator.aggregate(mrReaderService)).thenReturn(new ArrayList<>(
                List.of(
                        LLMMessage.builder().role(LLMMessage.Role.USER).text("Content1").build()
                )
        ));
        MRReview response = new MRReview();
        when(llmService.generate(anyList(), eq(MRReview.class), any(LLMConfig.class))).thenReturn(response);

        // When
        MRReview review = mergeRequestReviewService.review(mrReaderService);

        // Then
        verify(llmService).generate(List.of(
                LLMMessage.builder().role(LLMMessage.Role.USER).text("Content1").build()
        ), MRReview.class, LLMConfig.builder().prompt("시스템 프롬프트").build());
        assertEquals(response, review);
    }
}
