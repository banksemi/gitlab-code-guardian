package kr.easylab.gitlab_code_guardian.review.service;

import kr.easylab.gitlab_code_guardian.provider.notify.service.NotificationService;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

class MergeRequestPipelineTest {

    @Test
    void run_ShouldReviewAndSendNotificationInOrder() {
        // Given
        MRReaderService mockMRReaderService = mock(MRReaderService.class);
        MergeRequestReviewService mockMergeRequestReviewService = mock(MergeRequestReviewService.class);
        NotificationService mockNotificationService = mock(NotificationService.class);
        MergeRequestPipeline mergeRequestPipeline = new MergeRequestPipeline(
                mockMergeRequestReviewService, mockNotificationService
        );
        MRReview reviewResult = new MRReview();
        when(mockMergeRequestReviewService.review(mockMRReaderService)).thenReturn(reviewResult);

        // When
        mergeRequestPipeline.run(mockMRReaderService);

        // Then
        InOrder inOrder = inOrder(mockMergeRequestReviewService, mockNotificationService);
        inOrder.verify(mockMergeRequestReviewService, times(1)).review(mockMRReaderService);
        inOrder.verify(mockNotificationService, times(1)).sendNotification(reviewResult);
    }
}