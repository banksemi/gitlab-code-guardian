package kr.easylab.gitlab_code_guardian.review.service;

import kr.easylab.gitlab_code_guardian.provider.notify.service.NotificationService;
import kr.easylab.gitlab_code_guardian.provider.scm.service.GitlabMRContext;
import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

class MergeRequestPipelineTest {

    @Test
    void run_ShouldReviewAndSendNotificationInOrder() {
        // Given
        MergeRequestReviewService mockMergeRequestReviewService = mock(MergeRequestReviewService.class);
        NotificationService mockNotificationService = mock(NotificationService.class);
        ReviewConditionChecker mockReviewConditionChecker = mock(ReviewConditionChecker.class);

        MergeRequestPipeline mergeRequestPipeline = new MergeRequestPipeline(
                mockMergeRequestReviewService, mockNotificationService, mockReviewConditionChecker
        );
        MRReview reviewResult = MRReview.builder().build();
        when(mockMergeRequestReviewService.review()).thenReturn(reviewResult);
        when(mockReviewConditionChecker.isAllowed()).thenReturn(true);

        // When
        mergeRequestPipeline.runAndNotify();

        // Then
        InOrder inOrder = inOrder(mockMergeRequestReviewService, mockNotificationService);
        inOrder.verify(mockMergeRequestReviewService, times(1)).review();
        inOrder.verify(mockNotificationService, times(1)).sendNotification(reviewResult);
    }
}