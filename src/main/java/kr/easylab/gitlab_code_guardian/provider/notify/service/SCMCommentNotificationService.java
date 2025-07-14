package kr.easylab.gitlab_code_guardian.provider.notify.service;

import kr.easylab.gitlab_code_guardian.provider.scm.service.CommentWriterService;
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
    private final CommentWriterService commentWriterService;

    @Override
    public void sendNotification(MRReview review) {
        commentWriterService.writeComment(
                review.getSummary()
        );

        for (CodeBlockReview suggestion : review.getSuggestions()) {
            commentWriterService.writeComment(
                    suggestion.getFilePath(),
                    suggestion.getEndLine(),
                    suggestion.getComment()
            );
        }
    }

    @Override
    public void sendNotification(String message) {
        commentWriterService.writeComment(message);
    }
}
