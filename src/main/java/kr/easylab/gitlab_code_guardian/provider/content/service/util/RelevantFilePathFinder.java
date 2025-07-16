package kr.easylab.gitlab_code_guardian.provider.content.service.util;

import kr.easylab.gitlab_code_guardian.provider.content.dto.FilePathsResponse;

import java.util.List;

public interface RelevantFilePathFinder {
    FilePathsResponse findRelevantFilePaths(List<String> filePaths, String diffContent);
}
