package kr.easylab.gitlab_code_guardian.provider.content.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.DiffFile;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiffContentProvider implements ContentProvider {
    private final MRReaderService mrReaderService;

    @Override
    public String getTitle() {
        return "Diff";
    }

    @Override
    public String getContentText() {
        List<DiffFile> diffs = mrReaderService.getDiff();

        // TODO: 현재는 DiffFile 객체의 toString에 의존, 향후 LLM이 잘 인식할 수 있는 방향으로 개선 필요
        return diffs.toString();
    }
}
