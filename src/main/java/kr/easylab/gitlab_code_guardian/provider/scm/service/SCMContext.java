package kr.easylab.gitlab_code_guardian.provider.scm.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.SCMInformation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.DiffRef;
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
public class SCMContext {
    private SCMInformation SCMInformation;
}
