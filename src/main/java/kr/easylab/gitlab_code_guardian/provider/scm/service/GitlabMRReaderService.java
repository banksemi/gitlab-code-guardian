package kr.easylab.gitlab_code_guardian.provider.scm.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.DiffFile;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.MRDiscussion;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.MessageBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitlabMRReaderService implements MRReaderService {
    private final GitlabMRContext gitlabMRContext;

    @Override
    public String getBranchName() {
        return gitlabMRContext.getMergeRequest().getSourceBranch();
    }

    @Override
    public String getTitle() {
        return gitlabMRContext.getMergeRequest().getTitle();
    }

    @Override
    public MessageBase getInfo() {
        return MessageBase.builder()
                .authorId(gitlabMRContext.getMergeRequest().getAuthor().getUsername())
                .body(gitlabMRContext.getMergeRequest().getDescription())
                .createdAt(gitlabMRContext.getMergeRequest().getCreatedAt())
                .build();
    }

    @Override
    public List<MRDiscussion> getThreads() {
        return gitlabMRContext.getDiscussions().stream()
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

    }

    @Override
    public List<String> getFilePaths() {
        List<String> allFilePaths = new ArrayList<>();
        String commitId = getLatestCommitId().orElseThrow(() -> new RuntimeException("최근 커밋 정보를 찾을 수 없음"));
        String branchName = getBranchName();
        List<TreeItem> treeItems = null;
        try {
            treeItems = gitlabMRContext.getGitLabApi()
                    .getRepositoryApi()
                    .getTree(gitlabMRContext.getRepositoryId(), "", commitId, true);
            for (TreeItem item : treeItems) {
                if (item.getType() == TreeItem.Type.BLOB) {
                    log.debug("파일 경로 추가 (path: {}, repositoryId: {}, branchName: {})", item.getPath(), gitlabMRContext.getRepositoryId(), branchName);
                    allFilePaths.add(item.getPath());
                }
            }
        } catch (GitLabApiException e) {
            throw new RuntimeException(e);
        }
        return allFilePaths;
    }

    private Optional<String> getLatestCommitId() {
        List<String> filePaths = new ArrayList<>();

        List<Commit> commits = null;
        try {
            commits = gitlabMRContext.getGitLabApi()
                    .getMergeRequestApi()
                    .getCommits(gitlabMRContext.getRepositoryId(), gitlabMRContext.getMrId());
        } catch (GitLabApiException e) {
            log.warn("GitLab API 에러 발생 (repositoryId: {}, mrId: {}, branchName: {})",
                    gitlabMRContext.getRepositoryId(),
                    gitlabMRContext.getMrId(),
                    getBranchName());
            return Optional.empty();
        }

        if (commits.isEmpty()) {
            log.warn("MR에서 커밋 정보를 찾을 수 없음");
            return Optional.empty();
        }

        return Optional.of(commits.get(commits.size() - 1).getId());
    }

    @Override
    public String getFileContent(String filePath) {
        try {
            Optional<String> latestCommitId = getLatestCommitId();
            if (latestCommitId.isEmpty())
                throw new RuntimeException("최근 커밋 정보를 찾을 수 없음");

            // MR의 소스 브랜치에서 파일 내용 조회
            return gitlabMRContext.getGitLabApi()
                    .getRepositoryFileApi()
                    .getFile(gitlabMRContext.getRepositoryId(), filePath, latestCommitId.get())
                    .getContent();
        } catch (GitLabApiException e) {
            log.error("파일 내용을 가져오는데 실패했습니다.", e);
            return null;
        }
    }

    @Override
    public List<DiffFile> getDiff() {
        try {
            MergeRequest mr = gitlabMRContext.getMergeRequest();

            // 브랜치 (소스, 타켓)은 변경되었을 수 있으니 MR에 명시된 Ref로 직접 비교
            DiffRef refs = mr.getDiffRefs();
            CompareResults compareResults = gitlabMRContext.getGitLabApi()
                    .getRepositoryApi()
                    .compare(gitlabMRContext.getRepositoryId(), refs.getBaseSha(), refs.getHeadSha(), null, false);

            return compareResults.getDiffs()
                    .stream()
                    .map(d ->
                    DiffFile.builder()
                            .a_mode(d.getAMode())
                            .b_mode(d.getBMode())
                            .diff(d.getDiff())
                            .newPath(d.getNewPath())
                            .oldPath(d.getOldPath())
                            .renamed(d.getRenamedFile())
                            .deleted(d.getDeletedFile())
                            .created(d.getNewFile())
                            .build()
                    ).toList();
        } catch (GitLabApiException e) {
            log.error("Diff 정보를 가져오는데 실패했습니다.", e);
            throw new IllegalStateException("Diff 정보를 가져오는 데 실패했습니다.", e);
        }

    }

}
