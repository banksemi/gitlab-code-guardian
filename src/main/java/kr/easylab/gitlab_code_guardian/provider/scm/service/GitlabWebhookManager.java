package kr.easylab.gitlab_code_guardian.provider.scm.service;

import kr.easylab.gitlab_code_guardian.review.service.MergeRequestPipeline;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.WebHookManager;
import org.gitlab4j.api.webhook.NoteEvent;
import org.gitlab4j.api.webhook.WebHookListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GitlabWebhookManager extends WebHookManager {
    public GitlabWebhookManager(
            GitlabMRContext gitlabMRContext,
            MergeRequestPipeline mergeRequestPipeline,
            @Value("${gitlab.webhook_secret}") String webhookSecret
    ) {
        super(webhookSecret);

        addListener(new WebHookListener() {
            @Override
            public void onNoteEvent(NoteEvent noteEvent) {
                String repositoryId = noteEvent.getProject().getPathWithNamespace();
                Long mrId   = noteEvent.getMergeRequest().getIid();

                gitlabMRContext.setMrId(mrId);
                gitlabMRContext.setRepositoryId(repositoryId);
                gitlabMRContext.setNoteEvent(noteEvent);
                mergeRequestPipeline.runAndNotify();
            }
        });
        log.info("Gitlab Webhook Manager Initialized.");
    }
}
