package kr.easylab.gitlab_code_guardian.provider.content.service.mr;

import kr.easylab.gitlab_code_guardian.provider.content.service.ContentProvider;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MRTitleContentProviderImpl implements ContentProvider {
    private final MRReaderService mrReaderService;

    @Override
    public String getTitle() {
        return "MR 제목";
    }

    @Override
    public Optional<String> getContentText() {
        if (!mrReaderService.isAvailable())
            return Optional.empty();

        String title = mrReaderService.getTitle();
        if (title == null || title.isBlank())
            return Optional.empty();
        return Optional.of(title);
    }
}