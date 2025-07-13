package kr.easylab.gitlab_code_guardian.review.service;

import kr.easylab.gitlab_code_guardian.llm.dto.LLMMessage;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;

import java.util.List;

public interface ContentAggregator {
    List<LLMMessage> aggregate(MRReaderService mrReaderService);
}
