package kr.easylab.gitlab_code_guardian.provider.content.service.sha;

import kr.easylab.gitlab_code_guardian.provider.content.service.ContentProvider;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.DiffFile;
import kr.easylab.gitlab_code_guardian.provider.scm.service.ShaFileSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DiffContentProvider implements ContentProvider {
    private final ShaFileSnapshotService shaFileSnapshotService;

    @Override
    public String getTitle() {
        return "Diff";
    }

    @Override
    public Optional<String> getContentText() {
        List<DiffFile> diffs = shaFileSnapshotService.getDiff();
        StringBuilder sb = new StringBuilder();
        for (DiffFile diff : diffs) {
            String filePath = (diff.getNewPath() != null && !diff.getNewPath().isEmpty())
                    ? diff.getNewPath()
                    : diff.getOldPath();

            sb.append("--- File: ")
                    .append(filePath)
                    .append(" ---")
                    .append(System.lineSeparator())
                    .append(diff.getDiff())           // 실제 diff 내용
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());

        }
        if (sb.isEmpty())
            return Optional.empty();
        else
            return Optional.of(sb.toString().trim());
    }
}
