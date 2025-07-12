package kr.easylab.gitlab_code_guardian.provider.scm.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MergeRequestThread extends MessageBase {
    private List<MessageBase> comments;
}
