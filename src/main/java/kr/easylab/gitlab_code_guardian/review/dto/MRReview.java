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

    @ArraySchema(minItems = 1, maxItems = 10, schema = @Schema(implementation = CodeBlockReview.class, description = "코드 지정을 통해 수정을 제안할 부분"))
    private final List<CodeBlockReview> suggestions = List.of();
}
