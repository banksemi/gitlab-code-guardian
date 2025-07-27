package kr.easylab.gitlab_code_guardian.provider.scm.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.MRDiscussion;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.MessageBase;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.SCMInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.DiffRef;
import org.gitlab4j.api.models.Discussion;
import org.gitlab4j.api.models.MergeRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitlabMRReaderService implements MRReaderService {
    private final GitLabApi gitLabApi;
    private final SCMContext scmContext;
    private Long mrIdForShaUpdating;

    @Override
    public Boolean isAvailable() {
        if (scmContext.getSCMInformation() == null)
            return false;

        Long mrId = scmContext.getSCMInformation().getMrId();
        return mrId != null;
    }

    private Optional<MergeRequest> getMergeRequest() {
        try {
            return Optional.of(gitLabApi.getMergeRequestApi().getMergeRequest(
                    scmContext.getSCMInformation().getRepositoryId(),
                    scmContext.getSCMInformation().getMrId()
            ));
        } catch (GitLabApiException e) {
            log.error("MergeRequest 정보를 가져오는데 실패했습니다.", e);
            return Optional.empty();
        }
    }

    @Override
    public String getTitle() {
        Optional<MergeRequest> mergeRequest = getMergeRequest();
        return mergeRequest.map(MergeRequest::getTitle).orElse(null);

    }

    @Override
    public MessageBase getInfo() {
        Optional<MergeRequest> mergeRequest = getMergeRequest();
        if (mergeRequest.isEmpty())
            return null;
        MergeRequest mr = mergeRequest.get();

        return MessageBase.builder()
                .authorId(mr.getAuthor().getUsername())
                .body(mr.getDescription())
                .createdAt(mr.getCreatedAt())
                .build();
    }

    @Override
    public List<MRDiscussion> getThreads() {
        try {
            List<Discussion> mergeRequestDiscussions = gitLabApi
                    .getDiscussionsApi()
                    .getMergeRequestDiscussions(
                            scmContext.getSCMInformation().getRepositoryId(),
                            scmContext.getSCMInformation().getMrId());


            return mergeRequestDiscussions.stream()
                    .map(discussion -> MRDiscussion.builder()
                            .id(discussion.getId())
                            .comments(
                                    discussion.getNotes().stream()
                                            .filter(note -> !note.getSystem()) // System note 제외
                                            .map(
                                                    note -> MessageBase.builder()
                                                            .id(note.getId().toString())
                                                            .body(note.getBody())
                                                            .authorId(note.getAuthor().getUsername())
                                                            .createdAt(note.getCreatedAt())
                                                            .build())
                                            .collect(Collectors.toList()))
                            .build())
                    .filter(discussion -> !discussion.getComments().isEmpty())
                    .collect(Collectors.toList());
        } catch (GitLabApiException e) {
            log.error("MergeRequest 정보를 가져오는데 실패했습니다.", e);
            throw new IllegalStateException("MergeRequest 정보를 가져오는데 실패했습니다.", e);
        }
    }

    @Override
    public void updateShaFromMR() {
        Long newMrId = null;
        SCMInformation scmInformation = scmContext.getSCMInformation();
        if (scmInformation != null) {
            newMrId = scmInformation.getMrId();
        }
        if (!Objects.equals(newMrId, this.mrIdForShaUpdating)) {
            if (newMrId != null) {
                try {
                    MergeRequest mr = gitLabApi.getMergeRequestApi().getMergeRequest(
                            scmInformation.getRepositoryId(),
                            newMrId
                    );
                    DiffRef refs = mr.getDiffRefs();
                    scmInformation.setBaseSha(refs.getBaseSha());
                    scmInformation.setHeadSha(refs.getHeadSha());
                    log.info("BaseSha: {}, HeadSha: {}", refs.getBaseSha(), refs.getHeadSha());
                } catch (GitLabApiException e) {
                    log.error("SHA를 업데이트하는데 실패했습니다.", e);
                    throw new IllegalStateException("SHA를 업데이트하는데 실패했습니다.", e);
                }
            }
            this.mrIdForShaUpdating = newMrId;
        }
    }
}
