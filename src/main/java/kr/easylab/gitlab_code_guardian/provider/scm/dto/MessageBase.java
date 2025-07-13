package kr.easylab.gitlab_code_guardian.provider.scm.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Builder
public class MessageBase {
    private String id;
    /**
     * 작성자 ID (@user1)
     */
    private String authorId;
    private Date createdAt;
    private String body;
}
