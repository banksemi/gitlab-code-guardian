package kr.easylab.gitlab_code_guardian.provider.scm.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.DiffFile;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.MRDiscussion;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.MessageBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitlabMRReaderService implements MRReaderService {
    private final GitlabMRContext gitlabMRContext;

    @Override
    public String getTitle() {
        return gitlabMRContext.getMergeRequest().getTitle();
    }

    @Override
    public MessageBase getInfo() {
        return MessageBase.builder()
                .authorId(gitlabMRContext.getMergeRequest().getAuthor().getUsername())
                .body(gitlabMRContext.getMergeRequest().getDescription())
                .createdAt(gitlabMRContext.getMergeRequest().getCreatedAt())
                .build();
    }

    @Override
    public List<MRDiscussion> getThreads() {
        return gitlabMRContext.getDiscussions().stream()
                .map(discussion -> MRDiscussion.builder()
                        .id(discussion.getId())
                        .comments(
                                discussion.getNotes().stream()
                                        .filter(note -> !note.getSystem()) // System note 제외
                                        .map(
                                        note -> MessageBase.builder()
                                            .id(note.getId().toString())
                                            .body(note.getBody())
                                        .authorId(note.getAuthor().getUsername())
                                        .createdAt(note.getCreatedAt())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .filter(discussion -> !discussion.getComments().isEmpty())
                .collect(Collectors.toList());

    }
}
