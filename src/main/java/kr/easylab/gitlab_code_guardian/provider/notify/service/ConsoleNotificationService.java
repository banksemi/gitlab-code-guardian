package kr.easylab.gitlab_code_guardian.provider.notify.service;

import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsoleNotificationService implements NotificationService {
    @Override
    public void sendNotification(MRReview review) {
        log.info("{}", review);
    }

    @Override
    public void sendNotification(String message) {
        log.info("단일 메세지: {}", message);
    }
}
