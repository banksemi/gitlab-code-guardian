package kr.easylab.gitlab_code_guardian.provider.scm.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.MergeRequestThread;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.MessageBase;

import java.util.List;

public interface MRReaderService {
    String getBranchName();
    String getTitle();
    MessageBase getInfo();
    List<MergeRequestThread> getThreads();

    List<String> getFilePaths();
    String getFileContent(String filePath);
}
