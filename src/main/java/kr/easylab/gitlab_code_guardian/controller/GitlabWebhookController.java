package kr.easylab.gitlab_code_guardian.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import kr.easylab.gitlab_code_guardian.provider.scm.service.GitlabMRContext;
import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.WebHookManager;
import org.gitlab4j.api.webhook.NoteEvent;
import org.gitlab4j.api.webhook.WebHookListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GitlabWebhookController {
    private final WebHookManager webHookManager;

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
