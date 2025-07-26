package kr.easylab.gitlab_code_guardian.provider.content.service;

import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;

import java.util.Optional;

public interface ContentProvider {
    public String getTitle();
    public Optional<String> getContentText();
}
