package kr.easylab.gitlab_code_guardian.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CodeBlockReview {
    public enum ReviewPriority {
        CRITICAL,
        HIGH,
        NORMAL,
        MINOR
    }
    @Schema(description = "이슈가 발견된 파일의 전체 경로")
    private final String filePath;

    @Schema(description = "코드 리뷰 영역의 시작 줄 번호")
    private final Long startLine;

    @Schema(description = "코드 리뷰 영역의 마지막 줄 번호")
    private final Long endLine;

    @Schema(description = "코드 리뷰 코멘트")
    private final String comment;

    @Schema(description = "우선순위")
    private final ReviewPriority priority;
}
