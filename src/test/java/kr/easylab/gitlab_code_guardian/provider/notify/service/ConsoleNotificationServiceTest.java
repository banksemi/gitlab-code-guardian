package kr.easylab.gitlab_code_guardian.provider.notify.service;

import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import nl.altindag.log.LogCaptor;
class ConsoleNotificationServiceTest {
    private ConsoleNotificationService consoleNotificationService = new ConsoleNotificationService();
    private LogCaptor logCaptor = LogCaptor.forClass(ConsoleNotificationService.class);

    @BeforeEach
    void setUp() {
        logCaptor.clearLogs();
    }
    @Test
    @DisplayName("String 메시지 알림 전송 테스트")
    void sendNotification() {
        String message = "test message";
        consoleNotificationService.sendNotification(message);
        assertTrue(logCaptor.getInfoLogs().contains(
                "단일 메세지: " + message
        ));

    }

    @Test
    @DisplayName("MR 리뷰 객체 알림 전송 테스트")
    void testSendNotification() {
        MRReview review = mock(MRReview.class);
        when(review.toString()).thenReturn("review");
        
        consoleNotificationService.sendNotification(review);
        assertTrue(logCaptor.getInfoLogs().contains(
                "review"
        ));
    }

    @Test
    @DisplayName("빌더 패턴으로 생성된 MR 리뷰 알림 전송 테스트")
    void testSendNotificationWithBuilderPattern() {
        MRReview review = MRReview.builder()
                .summary("Test Summary")
                .build();
        
        consoleNotificationService.sendNotification(review);

        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("Test Summary")));
    }
}