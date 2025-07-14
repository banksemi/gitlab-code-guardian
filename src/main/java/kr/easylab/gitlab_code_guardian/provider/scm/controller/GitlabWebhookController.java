package kr.easylab.gitlab_code_guardian.provider.scm.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import kr.easylab.gitlab_code_guardian.provider.scm.service.GitlabMRContext;
import kr.easylab.gitlab_code_guardian.review.service.MergeRequestPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.WebHookManager;
import org.gitlab4j.api.webhook.NoteEvent;
import org.gitlab4j.api.webhook.WebHookListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@Slf4j
@RequiredArgsConstructor
public class GitlabWebhookController {
    private final WebHookManager webHookManager;
    private final GitlabMRContext gitlabMRContext;
    private final MergeRequestPipeline mergeRequestPipeline;

    @PostConstruct
    public void init() {
        webHookManager.addListener(new WebHookListener() {
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
    }
    @PostMapping("/gitlab/webhook")
    public void handleWebhook(
            @RequestHeader("X-Gitlab-Event") String event,
            @RequestHeader("X-Gitlab-Token") String token,
            HttpServletRequest request) {
        log.info("Event: {}", event);
        log.info("Token: {}", token);
        try {
            webHookManager.handleEvent(request);
        } catch (GitLabApiException e) {
            throw new RuntimeException(e);
        }
    }
}
