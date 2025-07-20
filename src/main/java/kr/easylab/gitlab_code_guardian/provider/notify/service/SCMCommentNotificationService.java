package kr.easylab.gitlab_code_guardian.provider.notify.service;

import kr.easylab.gitlab_code_guardian.provider.notify.service.util.SuggestionFormatter;
import kr.easylab.gitlab_code_guardian.provider.scm.service.CommentSendingService;
import kr.easylab.gitlab_code_guardian.review.dto.CodeBlockReview;
import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Slf4j
@Primary
@Service
@RequiredArgsConstructor
public class SCMCommentNotificationService implements NotificationService {
    private final CommentSendingService commentSendingService;
    private final SuggestionFormatter suggestionFormatter;

    @Override
    public void sendNotification(MRReview review) {
        if (review == null) {
            log.warn("Null review: {}", review);
            return;
        }

        String summary = review.getSummary();
        if (summary != null && !summary.isEmpty()) {
            commentSendingService.writeComment(summary);
        } else {
            log.info("Null or empty summary: {}", summary);
        }

        for (CodeBlockReview suggestion : review.getSuggestions()) {
            commentSendingService.writeComment(
                    suggestion.getFilePath(),
                    suggestion.getEndLine(),
                    suggestionFormatter.format(suggestion)
            );
        }
    }

    @Override
    public void sendNotification(String message) {
        if (message == null || message.isEmpty()) {
            log.warn("Null or empty message: {}", message);
            return;
        }
        commentSendingService.writeComment(message);
    }
}
