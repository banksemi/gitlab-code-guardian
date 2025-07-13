package kr.easylab.gitlab_code_guardian.review.service;

import kr.easylab.gitlab_code_guardian.llm.dto.LLMMessage;
import kr.easylab.gitlab_code_guardian.provider.content.service.ContentProvider;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentAggregatorImpl implements ContentAggregator {
    private final List<ContentProvider> contentProviders;

    @Override
    public List<LLMMessage> aggregate() {
        List<LLMMessage> messages = new ArrayList<>();
        for (ContentProvider contentProvider : contentProviders) {
            String contentTitle = contentProvider.getTitle();

            String content = contentProvider.getContentText();
            if (content != null && !content.isEmpty()) {
                messages.add(
                        LLMMessage.builder()
                                .role(LLMMessage.Role.USER)
                                .text("## " + contentTitle + "\n" + content)
                                .build()
                );
            }
        }
        return messages;
    }
}
