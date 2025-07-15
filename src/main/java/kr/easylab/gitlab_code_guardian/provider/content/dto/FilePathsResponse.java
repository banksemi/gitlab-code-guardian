package kr.easylab.gitlab_code_guardian.provider.content.dto;

import lombok.Data;

import java.util.List;

@Data
public class FilePathsResponse {
    private List<String> filePaths;
}
