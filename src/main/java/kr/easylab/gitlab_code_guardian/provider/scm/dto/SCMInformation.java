package kr.easylab.gitlab_code_guardian.provider.scm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SCMInformation {
    private String repositoryId;

    private String baseSha;
    private String headSha;

    private Long mrId;
}
