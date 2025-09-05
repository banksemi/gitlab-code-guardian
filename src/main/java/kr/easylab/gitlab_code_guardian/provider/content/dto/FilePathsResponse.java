package kr.easylab.gitlab_code_guardian.provider.content.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class FilePathsResponse {
    @Schema(description = "파일의 정확한 경로 (파일 맵에 명시된 경로 사용)", maxLength = 10, minLength = 0)
    private List<String> filePaths;
}
