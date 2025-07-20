package kr.easylab.gitlab_code_guardian.provider.notify.service.util;

import kr.easylab.gitlab_code_guardian.review.dto.CodeBlockReview;

public interface SuggestionFormatter {
    String format(CodeBlockReview suggestion);
}
