package kr.easylab.gitlab_code_guardian.controller;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.SCMInformation;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import kr.easylab.gitlab_code_guardian.provider.scm.service.SCMContext;
import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import kr.easylab.gitlab_code_guardian.review.service.MergeRequestReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class GitlabAPIController {
    private final SCMContext scmContext;
    private final MergeRequestReviewService mergeRequestReviewService;
    private final MRReaderService mrReaderService;
    private @Value("${gitlab.repository}") String repositoryName;
    private @Value("${gitlab.webhook_secret}") String webhookSecret;

    @PostMapping("/gitlab/review/merge-requests/{mrId}")
    public MRReview review(
            @PathVariable Long mrId,
            @RequestHeader(name = "Authorization") String key
    ) {
        key = key.replace("Bearer ", "");
        if (!key.equals(webhookSecret)) {
            throw new IllegalArgumentException("Invalid key.");
        }
        scmContext.setSCMInformation(
                SCMInformation.builder()
                        .repositoryId(repositoryName)
                        .mrId(mrId)
                        .build()
        );
        mrReaderService.updateShaFromMR();
        MRReview review = mergeRequestReviewService.review();
        log.info("Review: {}", review);
        return review;
    }

    @PostMapping("/gitlab/review/ref")
    public MRReview review(
            @RequestBody RefRequest refRequest,
            @RequestHeader(name = "Authorization") String key
    ) {
        key = key.replace("Bearer ", "");
        if (!key.equals(webhookSecret)) {
            throw new IllegalArgumentException("Invalid key.");
        }
        scmContext.setSCMInformation(
                SCMInformation.builder()
                        .repositoryId(repositoryName)
                        .baseSha(refRequest.getBaseRef())
                        .headSha(refRequest.getHeadRef())
                        .build()
        );

        MRReview review = mergeRequestReviewService.review();
        log.info("Review: {}", review);
        return review;
    }
}
