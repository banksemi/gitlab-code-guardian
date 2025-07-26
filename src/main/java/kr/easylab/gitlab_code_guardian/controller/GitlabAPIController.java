package kr.easylab.gitlab_code_guardian.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import kr.easylab.gitlab_code_guardian.provider.scm.service.GitlabMRContext;
import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import kr.easylab.gitlab_code_guardian.review.service.MergeRequestReviewService;
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
public class GitlabAPIController {
    private final GitlabMRContext gitlabMRContext;
    private final MergeRequestReviewService mergeRequestReviewService;
    private @Value("${gitlab.repository}") String repositoryName;
    private @Value("${gitlab.webhook_secret}") String webhookSecret;

    @GetMapping("/gitlab/merge-requests/{mrId}/review")
    public MRReview review(
            @PathVariable Long mrId,
            @RequestHeader(name = "Authorization") String key
    ) {
        key = key.replace("Bearer ", "");
        if (!key.equals(webhookSecret)) {
            throw new IllegalArgumentException("Invalid key.");
        }
        gitlabMRContext.setMrId(mrId);
        gitlabMRContext.setRepositoryId(repositoryName);
        MRReview review = mergeRequestReviewService.review();
        log.info("Review: {}", review);
        return review;
    }
}
