package kr.easylab.gitlab_code_guardian.provider.content.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FilePathsResponse {
    private List<String> filePaths;
}
