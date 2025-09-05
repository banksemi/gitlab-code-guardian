package kr.easylab.gitlab_code_guardian.provider.scm.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.DiffFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShaFileSnapshotServiceImpl implements ShaFileSnapshotService {
    private final SCMContext scmContext;
    private final GitLabApi gitLabApi;

    @Override
    public List<String> getFilePaths() {
        List<String> allFilePaths = new ArrayList<>();
        List<TreeItem> treeItems = null;
        try {
            treeItems = gitLabApi
                    .getRepositoryApi()
                    .getTree(scmContext.getSCMInformation().getRepositoryId(), "", scmContext.getSCMInformation().getHeadSha(), true);
            for (TreeItem item : treeItems) {
                if (item.getType() == TreeItem.Type.BLOB) {
                    log.debug(
                            "파일 경로 추가 (path: {}, repositoryId: {})",
                            item.getPath(),
                            scmContext.getSCMInformation().getRepositoryId()
                    );
                    allFilePaths.add(item.getPath());
                }
            }
        } catch (GitLabApiException e) {
            throw new RuntimeException(e);
        }
        return allFilePaths;
    }

    @Override
    public Optional<String> getFileContent(String filePath) {
        try {
            log.info("파일 내용을 가져오는 중: {}", filePath);
            String decodedContent = gitLabApi
                    .getRepositoryFileApi()
                    .getFile(
                            scmContext.getSCMInformation().getRepositoryId(),
                            filePath,
                            scmContext.getSCMInformation().getHeadSha()
                    ).getDecodedContentAsString();
            return Optional.of(decodedContent);
        } catch (GitLabApiException e) {
            log.error("{} 파일 내용을 가져오는데 실패했습니다.", filePath, e);
            return Optional.empty();
        }
    }

    @Override
    public List<DiffFile> getDiff() {
        try {
            CompareResults compareResults = gitLabApi
                    .getRepositoryApi()
                    .compare(
                            scmContext.getSCMInformation().getRepositoryId(),
                            scmContext.getSCMInformation().getBaseSha() ,
                            scmContext.getSCMInformation().getHeadSha(),
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
