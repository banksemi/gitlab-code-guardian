package kr.easylab.gitlab_code_guardian.provider.content.service.mr;

import kr.easylab.gitlab_code_guardian.provider.content.service.ContentProvider;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.MessageBase;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MRBodyContentProvider implements ContentProvider {
    private final MRReaderService mrReaderService;

    @Override
    public String getTitle() {
        return "MR 본문";
    }

    @Override
    public Optional<String> getContentText() {
        if (!mrReaderService.isAvailable())
            return Optional.empty();

        MessageBase info = mrReaderService.getInfo();
        if (info == null)
            return Optional.empty();

        String body = info.getBody();
        if (body == null || body.isBlank())
            return Optional.empty();
        return Optional.of(body);
    }
}