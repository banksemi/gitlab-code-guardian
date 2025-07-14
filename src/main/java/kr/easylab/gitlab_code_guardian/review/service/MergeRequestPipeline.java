package kr.easylab.gitlab_code_guardian.review.service;

import kr.easylab.gitlab_code_guardian.provider.notify.service.NotificationService;
import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import kr.easylab.gitlab_code_guardian.review.exception.NotAllowedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MergeRequestPipeline {
    private final MergeRequestReviewService mergeRequestReviewService;
    private final NotificationService notificationService;
    private final ReviewConditionChecker reviewConditionChecker;

    public MRReview runAndNotify() throws NotAllowedException {
        if (!reviewConditionChecker.isAllowed()) {
            throw new NotAllowedException(
                    "Not allowed to review this merge request."
            );
        }

        notificationService.sendNotification("리뷰를 시작합니다.");
        MRReview review = mergeRequestReviewService.review();
        notificationService.sendNotification(review);
        return review;
    }
}
