package kr.easylab.gitlab_code_guardian.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CodeBlockReview {
    @Schema(description = "이슈가 발견된 파일의 전체 경로")
    private final String filePath;

    @Schema(description = "리뷰하는 코드 블럭의 마지막 라인")
    private final Long endLine;
    private final String comment;
}
