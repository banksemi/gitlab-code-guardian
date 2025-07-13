package kr.easylab.gitlab_code_guardian.prompt.user.service;

import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;

/**
 * 사용자가 임의로 추가할 프롬프트를 관리합니다.
 */
public interface UserPromptService {
    String getPrompt();
}
