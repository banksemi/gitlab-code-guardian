package kr.easylab.gitlab_code_guardian.provider.scm.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.DiffFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShaFileSnapshotServiceImpl implements ShaFileSnapshotService {
    private final GitlabMRContext gitlabMRContext;

    @Override
    public List<String> getFilePaths() {
        gitlabMRContext.updateShaFromMR();
        List<String> allFilePaths = new ArrayList<>();
        List<TreeItem> treeItems = null;
        try {
            treeItems = gitlabMRContext.getGitLabApi()
                    .getRepositoryApi()
                    .getTree(gitlabMRContext.getRepositoryId(), "", gitlabMRContext.getHeadSha(), true);
            for (TreeItem item : treeItems) {
                if (item.getType() == TreeItem.Type.BLOB) {
                    log.debug("파일 경로 추가 (path: {}, repositoryId: {})", item.getPath(), gitlabMRContext.getRepositoryId());
                    allFilePaths.add(item.getPath());
                }
            }
        } catch (GitLabApiException e) {
            throw new RuntimeException(e);
        }
        return allFilePaths;
    }

    @Override
    public String getFileContent(String filePath) {
        gitlabMRContext.updateShaFromMR();
    try {
            return gitlabMRContext.getGitLabApi()
                    .getRepositoryFileApi()
                    .getFile(gitlabMRContext.getRepositoryId(), filePath, gitlabMRContext.getHeadSha()).getDecodedContentAsString();

        } catch (GitLabApiException e) {
            log.error("파일 내용을 가져오는데 실패했습니다.", e);
            return null;
        }
    }

    @Override
    public List<DiffFile> getDiff() {
        gitlabMRContext.updateShaFromMR();
        try {
            CompareResults compareResults = gitlabMRContext
                    .getGitLabApi()
                    .getRepositoryApi()
                    .compare(
                            gitlabMRContext.getRepositoryId(),
                            gitlabMRContext.getBaseSha() ,
                            gitlabMRContext.getHeadSha(),
                            null,
                            false
                    );

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
