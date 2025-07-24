package kr.easylab.gitlab_code_guardian.review.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class MRReview {
    private final String summary;

    @Schema(description = "수정 제안 목록 (해결되지 않고 수정이 필요한 항목만 언급합니다)")
    @ArraySchema(
            minItems = 1,
            maxItems = 10,
            schema = @Schema(implementation = CodeBlockReview.class)
    )
    private final List<CodeBlockReview> suggestions = List.of();
}
