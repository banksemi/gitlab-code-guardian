package kr.easylab.gitlab_code_guardian.provider.scm.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageBase {
    private String id;
    /**
     * 작성자 ID (@user1)
     */
    private String authorId;
    private LocalDateTime createdDate;
    private String body;
}
