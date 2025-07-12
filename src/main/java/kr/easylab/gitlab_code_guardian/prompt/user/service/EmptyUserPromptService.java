package kr.easylab.gitlab_code_guardian.prompt.user.service;

import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import org.springframework.stereotype.Service;

@Service
public class EmptyUserPromptService implements UserPromptService {
    public String getPrompt(MRReaderService mrReaderService) {
        return "";
    }
}
