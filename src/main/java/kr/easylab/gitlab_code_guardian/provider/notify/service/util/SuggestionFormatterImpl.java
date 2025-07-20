package kr.easylab.gitlab_code_guardian.provider.notify.service.util;

import kr.easylab.gitlab_code_guardian.review.dto.CodeBlockReview;
import org.springframework.stereotype.Service;

@Service
public class SuggestionFormatterImpl implements SuggestionFormatter {
    @Override
    public String format(CodeBlockReview suggestion) {
        StringBuilder sb = new StringBuilder();
        sb.append("**Priority: ").append(suggestion.getPriority()).append("**");
        sb.append(System.lineSeparator()).append(System.lineSeparator());
        sb.append(suggestion.getComment());
        return sb.toString();
    }
}
