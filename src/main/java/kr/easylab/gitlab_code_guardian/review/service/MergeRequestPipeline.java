package kr.easylab.gitlab_code_guardian.review.service;

import kr.easylab.gitlab_code_guardian.provider.notify.service.NotificationService;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MergeRequestPipeline {
    private final MergeRequestReviewService mergeRequestReviewService;
    private final NotificationService notificationService;

    public void run(MRReaderService mrReaderService) {
        MRReview review = mergeRequestReviewService.review(mrReaderService);
        notificationService.sendNotification(review);
    }
}
