package kr.easylab.gitlab_code_guardian.provider.notify.service;

import kr.easylab.gitlab_code_guardian.provider.notify.service.util.SuggestionFormatter;
import kr.easylab.gitlab_code_guardian.provider.scm.service.CommentSendingService;
import kr.easylab.gitlab_code_guardian.review.dto.CodeBlockReview;
import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SCMCommentNotificationServiceTest {

    @Mock
    private CommentSendingService commentSendingService;

    @Mock
    private SuggestionFormatter suggestionFormatter;

    @InjectMocks
    private SCMCommentNotificationService scmCommentNotificationService;

    private MRReview testMRReview;
    private CodeBlockReview testCodeBlockReview;

    @BeforeEach
    void setUp() {
        testCodeBlockReview = new CodeBlockReview(
                "src/main/java/TestFile.java",
                10L,
                15L,
                "Test comment for code review",
                CodeBlockReview.ReviewPriority.HIGH
        );

        testMRReview = mock(MRReview.class);
    }

    @Test
    @DisplayName("MR 리뷰 전송 테스트")
    void sendNotification_WithMRReview_ShouldSendSummaryAndSuggestions() {
        // Given
        String testSummary = "Test MR summary";
        when(testMRReview.getSummary()).thenReturn(testSummary);
        when(testMRReview.getSuggestions()).thenReturn(Arrays.asList(testCodeBlockReview));
        
        String formattedSuggestion = "Formatted suggestion";
        when(suggestionFormatter.format(testCodeBlockReview)).thenReturn(formattedSuggestion);

        // When
        scmCommentNotificationService.sendNotification(testMRReview);

        // Then
        verify(commentSendingService).writeComment(testSummary);
        verify(commentSendingService).writeComment(
                "src/main/java/TestFile.java",
                15L,
                formattedSuggestion
        );
        verify(suggestionFormatter).format(testCodeBlockReview);
    }

    @Test
    @DisplayName("코드 제안 사항이 여러개인 케이스")
    void sendNotification_WithMRReviewWithMultipleSuggestions_ShouldSendSummaryAndAllSuggestions() {
        // Given
        CodeBlockReview secondSuggestion = new CodeBlockReview(
                "src/main/java/AnotherFile.java",
                20L,
                25L,
                "Another test comment",
                CodeBlockReview.ReviewPriority.NORMAL
        );

        String testSummary = "Test summary with multiple suggestions";
        MRReview reviewWithMultipleSuggestions = mock(MRReview.class);
        when(reviewWithMultipleSuggestions.getSummary()).thenReturn(testSummary);
        when(reviewWithMultipleSuggestions.getSuggestions()).thenReturn(Arrays.asList(testCodeBlockReview, secondSuggestion));

        String firstFormattedSuggestion = "First formatted suggestion";
        String secondFormattedSuggestion = "Second formatted suggestion";

        when(suggestionFormatter.format(testCodeBlockReview)).thenReturn(firstFormattedSuggestion);
        when(suggestionFormatter.format(secondSuggestion)).thenReturn(secondFormattedSuggestion);

        // When
        scmCommentNotificationService.sendNotification(reviewWithMultipleSuggestions);

        // Then
        verify(commentSendingService).writeComment(testSummary);
        verify(commentSendingService).writeComment(
                "src/main/java/TestFile.java",
                15L,
                firstFormattedSuggestion
        );
        verify(commentSendingService).writeComment(
                "src/main/java/AnotherFile.java",
                25L,
                secondFormattedSuggestion
        );
        verify(suggestionFormatter).format(testCodeBlockReview);
        verify(suggestionFormatter).format(secondSuggestion);
    }

    @Test
    @DisplayName("코드 제안 사항이 없더라도 요약은 전송")
    void sendNotification_WithMRReviewWithEmptySuggestions_ShouldSendOnlySummary() {
        // Given
        String testSummary = "Test summary with no suggestions";
        MRReview reviewWithEmptySuggestions = mock(MRReview.class);
        when(reviewWithEmptySuggestions.getSummary()).thenReturn(testSummary);
        when(reviewWithEmptySuggestions.getSuggestions()).thenReturn(Collections.emptyList());

        // When
        scmCommentNotificationService.sendNotification(reviewWithEmptySuggestions);

        // Then
        verify(commentSendingService).writeComment(testSummary);
        verify(suggestionFormatter, never()).format(any(CodeBlockReview.class));
        verify(commentSendingService, never()).writeComment(anyString(), anyLong(), anyString());
    }

    @Test
    @DisplayName("단순 문자열 메세지에 대한 호출 테스트")
    void sendNotification_WithStringMessage_ShouldSendMessage() {
        // Given
        String testMessage = "Test notification message";

        // When
        scmCommentNotificationService.sendNotification(testMessage);

        // Then
        verify(commentSendingService).writeComment(testMessage);
        verify(suggestionFormatter, never()).format(any(CodeBlockReview.class));
    }

    @Test
    @DisplayName("요약이 없더라도 코드 제안사항이 있는 경우 전송")
    void sendNotification_WithNullSummary_ShouldSendOnlySuggestions() {
        // Given
        MRReview reviewWithNullSummary = mock(MRReview.class);
        when(reviewWithNullSummary.getSummary()).thenReturn(null);
        when(reviewWithNullSummary.getSuggestions()).thenReturn(Arrays.asList(testCodeBlockReview));
        
        String formattedSuggestion = "Formatted suggestion";
        when(suggestionFormatter.format(testCodeBlockReview)).thenReturn(formattedSuggestion);

        // When
        scmCommentNotificationService.sendNotification(reviewWithNullSummary);

        // Then
        verify(commentSendingService).writeComment(
                "src/main/java/TestFile.java",
                15L,
                formattedSuggestion
        );
        verify(suggestionFormatter).format(testCodeBlockReview);
        verify(commentSendingService, never()).writeComment(anyString());
    }

    @Test
    @DisplayName("null 메세지는 전송 생략")
    void sendNotification_WithNullStringMessage_ShouldHandleGracefully() {
        // Given
        String nullMessage = null;

        // When
        scmCommentNotificationService.sendNotification(nullMessage);

        // Then
        verify(commentSendingService, never()).writeComment(anyString());
    }

    @Test
    @DisplayName("빈 문자열 메세지는 전송 생략")
    void sendNotification_WithEmptyStringMessage_ShouldHandleGracefully() {
        // Given
        String emptyMessage = "";

        // When
        scmCommentNotificationService.sendNotification(emptyMessage);

        // Then
        verify(commentSendingService, never()).writeComment(anyString());
    }

    @Test
    @DisplayName("리뷰 객체가 존재하지 않는 경우 아무런 처리를 하지 않음")
    void sendNotification_WithNullMRReview_ShouldHandleGracefully() {
        // Given
        MRReview nullReview = null;

        // When
        scmCommentNotificationService.sendNotification(nullReview);

        // Then
        verify(commentSendingService, never()).writeComment(anyString());
        verify(commentSendingService, never()).writeComment(anyString(), anyLong(), anyString());
        verify(suggestionFormatter, never()).format(any(CodeBlockReview.class));
    }

    @Test
    @DisplayName("MR 요약 사항이 비어있는 경우 코드 블럭 제안 사항만 처리")
    void sendNotification_WithEmptySummary_ShouldSendOnlySuggestions() {
        // Given
        MRReview reviewWithEmptySummary = mock(MRReview.class);
        when(reviewWithEmptySummary.getSummary()).thenReturn("");
        when(reviewWithEmptySummary.getSuggestions()).thenReturn(Arrays.asList(testCodeBlockReview));
        
        String formattedSuggestion = "Formatted suggestion";
        when(suggestionFormatter.format(testCodeBlockReview)).thenReturn(formattedSuggestion);

        // When
        scmCommentNotificationService.sendNotification(reviewWithEmptySummary);

        // Then
        verify(commentSendingService).writeComment(
                "src/main/java/TestFile.java",
                15L,
                formattedSuggestion
        );
        verify(suggestionFormatter).format(testCodeBlockReview);
        verify(commentSendingService, never()).writeComment(anyString());
    }

    @Test
    @DisplayName("요약과 제안 사항이 없는 빈 MRReview")
    void sendNotification_WithNullSummaryAndEmptySuggestions_ShouldNotSendAnything() {
        // Given
        MRReview reviewWithNullSummaryAndEmptySuggestions = mock(MRReview.class);
        when(reviewWithNullSummaryAndEmptySuggestions.getSummary()).thenReturn(null);
        when(reviewWithNullSummaryAndEmptySuggestions.getSuggestions()).thenReturn(Collections.emptyList());

        // When
        scmCommentNotificationService.sendNotification(reviewWithNullSummaryAndEmptySuggestions);

        // Then
        verify(commentSendingService, never()).writeComment(anyString());
        verify(commentSendingService, never()).writeComment(anyString(), anyLong(), anyString());
        verify(suggestionFormatter, never()).format(any(CodeBlockReview.class));
    }
}