package kr.easylab.gitlab_code_guardian.provider.notify.service.util;

import kr.easylab.gitlab_code_guardian.review.dto.CodeBlockReview;
import kr.easylab.gitlab_code_guardian.review.dto.CodeBlockReview.ReviewPriority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SuggestionFormatterImplTest {

    private SuggestionFormatterImpl suggestionFormatter;

    @BeforeEach
    void setUp() {
        suggestionFormatter = new SuggestionFormatterImpl();
    }

    @Test
    @DisplayName("SuggestionFormatter 기본 테스트")
    void format_shouldReturnFormattedString_whenGivenCodeBlockReview() {
        // Given
        CodeBlockReview suggestion = new CodeBlockReview(
            "TestFile.java", 
            1L, 
            5L, 
            "This is a test comment", 
            ReviewPriority.HIGH
        );

        // When
        String result = suggestionFormatter.format(suggestion);

        // Then
        String expected = "**Priority: HIGH**" + System.lineSeparator() + System.lineSeparator() + "This is a test comment";
        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("우선순위가 정상적으로 출력되는지 확인")
    void format_shouldHandleAllPriorityLevels() {
        // Given & When & Then
        CodeBlockReview criticalReview = new CodeBlockReview("file.java", 1L, 1L, "Critical issue", ReviewPriority.CRITICAL);
        String criticalResult = suggestionFormatter.format(criticalReview);
        assertThat(criticalResult).contains("**Priority: CRITICAL**");

        CodeBlockReview highReview = new CodeBlockReview("file.java", 1L, 1L, "High issue", ReviewPriority.HIGH);
        String highResult = suggestionFormatter.format(highReview);
        assertThat(highResult).contains("**Priority: HIGH**");

        CodeBlockReview normalReview = new CodeBlockReview("file.java", 1L, 1L, "Normal issue", ReviewPriority.NORMAL);
        String normalResult = suggestionFormatter.format(normalReview);
        assertThat(normalResult).contains("**Priority: NORMAL**");

        CodeBlockReview minorReview = new CodeBlockReview("file.java", 1L, 1L, "Minor issue", ReviewPriority.MINOR);
        String minorResult = suggestionFormatter.format(minorReview);
        assertThat(minorResult).contains("**Priority: MINOR**");
    }
}