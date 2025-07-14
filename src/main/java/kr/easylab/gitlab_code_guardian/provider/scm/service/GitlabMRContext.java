package kr.easylab.gitlab_code_guardian.provider.scm.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Discussion;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.webhook.NoteEvent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Scope(value="request", proxyMode= ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
@Slf4j
public class GitlabMRContext {
    private final GitLabApi gitLabApi;
    private final String sessionId = UUID.randomUUID().toString();

    private Long mrId;
    private String repositoryId;

    private NoteEvent noteEvent;

    @Cacheable(
            value = "mergeRequest",
            key = "#root.target.sessionId + '_' + #root.target.repositoryId + '_' + #root.target.mrId"
    )
    public MergeRequest getMergeRequest() {
        log.info("Requesting gitLab API for merge request (sessionId: {}, repositoryId: {}, mrId: {})",
                getSessionId(),
                getRepositoryId(),
                getMrId());
        MergeRequest mergeRequest = null;
        try {
            mergeRequest = gitLabApi.getMergeRequestApi().getMergeRequest(
                    getRepositoryId(),
                    getMrId()
            );
        } catch (GitLabApiException e) {
            throw new RuntimeException(e);
        }
        return mergeRequest;
    }

    @Cacheable(value = "discussions", key="#root.target.sessionId + '_' + #root.target.repositoryId + '_' + #root.target.mrId")
    public List<Discussion> getDiscussions() {
        try {
            return getGitLabApi()
                    .getDiscussionsApi()
                    .getMergeRequestDiscussions(getRepositoryId(),
                            getMrId());
        } catch (GitLabApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void validateContext() {
        if (getRepositoryId() == null || getRepositoryId().isEmpty()) {
            throw new IllegalArgumentException("repositoryId is required.");
        }
        if (getMrId() == null) {
            throw new IllegalArgumentException("mrId is required.");
        }
    }
}
