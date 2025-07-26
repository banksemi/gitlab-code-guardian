package kr.easylab.gitlab_code_guardian.provider.content.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.DiffFile;
import kr.easylab.gitlab_code_guardian.provider.scm.service.ShaFileSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiffContentProvider implements ContentProvider {
    private final ShaFileSnapshotService shaFileSnapshotService;

    @Override
    public String getTitle() {
        return "Diff";
    }

    @Override
    public String getContentText() {
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
        return sb.toString();
    }
}
