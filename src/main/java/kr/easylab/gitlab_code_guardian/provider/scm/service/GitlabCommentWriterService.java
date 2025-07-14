package kr.easylab.gitlab_code_guardian.provider.scm.service;

import lombok.RequiredArgsConstructor;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.DiffRef;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Position;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GitlabCommentWriterService implements CommentWriterService {
    private final GitlabMRContext gitlabMRContext;
    private final GitLabApi gitLabApi;

    @Override
    public void writeComment(String content) {
        try {
            gitLabApi.getDiscussionsApi().createMergeRequestDiscussion(
                    gitlabMRContext.getRepositoryId(),
                    gitlabMRContext.getMrId(),
                    content,
                    null,
                    null,
                    null
            );
        } catch (GitLabApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeComment(String filePath, Long fileLine, String content) {
        try {
            MergeRequest mr = gitlabMRContext.getMergeRequest();
            DiffRef refs = mr.getDiffRefs();

            Position position = new Position()
                    .withBaseSha(refs.getBaseSha())
                    .withStartSha(refs.getStartSha())
                    .withHeadSha(refs.getHeadSha())
                    .withPositionType(Position.PositionType.TEXT)
                    .withNewPath(filePath)
                    .withNewLine(fileLine.intValue());

            gitLabApi.getDiscussionsApi().createMergeRequestDiscussion(
                    gitlabMRContext.getRepositoryId(),
                    gitlabMRContext.getMrId(),
                    content,
                    null,
                    null,
                    position
            );
        } catch (GitLabApiException e) {
            // Failover
            StringBuilder sb = new StringBuilder();
            sb.append("File: ");
            sb.append(filePath);
            sb.append(", Line: ");
            sb.append(fileLine);
            sb.append(System.lineSeparator()).append(System.lineSeparator());
            sb.append(content);
            writeComment(sb.toString());
        }

    }
}
