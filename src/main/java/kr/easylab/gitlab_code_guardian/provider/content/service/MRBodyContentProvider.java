package kr.easylab.gitlab_code_guardian.provider.content.service;

import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MRBodyContentProvider implements ContentProvider {
    private final MRReaderService mrReaderService;

    @Override
    public String getTitle() {
        return "MR 본문";
    }

    @Override
    public String getContentText() {
        return mrReaderService.getInfo().getBody();
    }
}