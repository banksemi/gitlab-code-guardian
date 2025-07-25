package kr.easylab.gitlab_code_guardian.prompt.user.service;

import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@ExtendWith(MockitoExtension.class)
class EmptyUserPromptServiceTest {
    @Test
    void getPrompt_shouldReturnEmptyString() {
        // Given
        EmptyUserPromptService emptyUserPromptService = new EmptyUserPromptService();

        // When
        String result = emptyUserPromptService.getPrompt();

        // Then
        assertNotNull(result);
        assertEquals("", result);
    }
}