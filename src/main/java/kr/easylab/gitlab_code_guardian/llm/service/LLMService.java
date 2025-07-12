package kr.easylab.gitlab_code_guardian.llm.service;

import kr.easylab.gitlab_code_guardian.llm.dto.LLMConfig;
import kr.easylab.gitlab_code_guardian.llm.dto.LLMMessage;

import java.util.List;

public interface LLMService {
    <T> T generate(List<LLMMessage> messages, Class<T> clazz, LLMConfig config);
}
