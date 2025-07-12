package kr.easylab.gitlab_code_guardian.provider.scm.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.MergeRequestThread;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.MessageBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GitlabMRReaderService implements MRReaderService {
    private final GitlabMRContext gitlabMRContext;

    @Override
    public String getBranchName() {
        return "";
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public MessageBase getInfo() {
        return null;
    }

    @Override
    public List<MergeRequestThread> getThreads() {
        return List.of();
    }

    @Override
    public List<String> getFilePaths() {
        return List.of();
    }

    @Override
    public String getFileContent(String filePath) {
        return "";
    }
}
