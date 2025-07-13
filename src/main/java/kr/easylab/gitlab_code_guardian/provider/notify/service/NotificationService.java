package kr.easylab.gitlab_code_guardian.provider.notify.service;

import kr.easylab.gitlab_code_guardian.review.dto.MRReview;

/**
 * MR 리뷰를 게시할 서비스
 */
public interface NotificationService {
    void sendNotification(MRReview review);
}
