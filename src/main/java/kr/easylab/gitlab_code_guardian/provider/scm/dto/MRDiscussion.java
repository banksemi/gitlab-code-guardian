package kr.easylab.gitlab_code_guardian.provider.scm.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
public class MRDiscussion {
    private String id;
    private List<MessageBase> comments;
}
