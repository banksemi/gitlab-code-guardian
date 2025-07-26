package kr.easylab.gitlab_code_guardian.provider.content.service.sha;

import kr.easylab.gitlab_code_guardian.provider.content.service.ContentProvider;
import kr.easylab.gitlab_code_guardian.provider.scm.service.ShaFileSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileMapContentProvider implements ContentProvider {
    private final ShaFileSnapshotService shaFileSnapshotService;

    @Override
    public String getTitle() {
        return "FileMap (모든 파일 목록)";
    }

    @Override
    public Optional<String> getContentText() {
        List<String> diffs = shaFileSnapshotService.getFilePaths();
        return Optional.of(String.join("\n", diffs));
    }
}
