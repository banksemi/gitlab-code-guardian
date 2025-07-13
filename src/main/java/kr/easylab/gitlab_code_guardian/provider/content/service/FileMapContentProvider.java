package kr.easylab.gitlab_code_guardian.provider.content.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.DiffFile;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileMapContentProvider implements ContentProvider {
    private final MRReaderService mrReaderService;

    @Override
    public String getTitle() {
        return "FileMap (모든 파일 목록)";
    }

    @Override
    public String getContentText() {
        List<String> diffs = mrReaderService.getFilePaths();
        return String.join("\n", diffs);
    }
}
