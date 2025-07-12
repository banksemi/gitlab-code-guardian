package kr.easylab.gitlab_code_guardian.provider.content.service;

import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;

public class MRTitleContentProviderImpl implements ContentProvider {
    @Override
    public String getTitle() {
        return "MR 제목";
    }

    @Override
    public String getContentText(MRReaderService mrReaderService) {
        return mrReaderService.getTitle();
    }
}