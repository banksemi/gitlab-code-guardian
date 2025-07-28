package kr.easylab.gitlab_code_guardian.provider.scm.service;

import kr.easylab.gitlab_code_guardian.provider.notify.service.NotificationService;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.SCMInformation;
import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import kr.easylab.gitlab_code_guardian.review.exception.NotAllowedException;
import kr.easylab.gitlab_code_guardian.review.service.MergeRequestReviewService;
import kr.easylab.gitlab_code_guardian.review.service.ReviewConditionChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.webhook.NoteEvent;
import org.gitlab4j.api.webhook.WebHookListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoteEventWebHookListener implements WebHookListener {
    private final SCMContext scmContext;
    private final MergeRequestReviewService mergeRequestReviewService;
    private final ReviewConditionChecker reviewConditionChecker;
    private final NotificationService notificationService;
    private final MRReaderService mrReaderService;

    @Override
    public void onNoteEvent(NoteEvent noteEvent) {
        String repositoryId = noteEvent.getProject().getPathWithNamespace();
        Long mrId = noteEvent.getMergeRequest().getIid();
        scmContext.setSCMInformation(
                SCMInformation.builder()
                        .repositoryId(repositoryId)
                        .mrId(mrId)
                        .build()
        );

        if (!reviewConditionChecker.isAllowed(
                noteEvent
        )) {
            throw new NotAllowedException(
                    "Not allowed to review this merge request."
            );
        }

        mrReaderService.updateShaFromMR();
        notificationService.sendNotification("리뷰를 시작합니다.");
        MRReview review = mergeRequestReviewService.review();
        notificationService.sendNotification(review);
    }
}
