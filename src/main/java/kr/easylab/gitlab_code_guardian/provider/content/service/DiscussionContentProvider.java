package kr.easylab.gitlab_code_guardian.provider.content.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.MRDiscussion;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.MessageBase;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscussionContentProvider implements ContentProvider {
    private final MRReaderService mrReaderService;

    @Override
    public String getTitle() {
        return "MR Comment History";
    }

    @Override
    public String getContentText() {
        List<MRDiscussion> discussions = mrReaderService.getThreads();

        if (discussions == null || discussions.isEmpty()) {
            return "";
        }

        StringBuilder discussionsText = new StringBuilder();

        for (MRDiscussion discussion : discussions) {
            List<MessageBase> comments = discussion.getComments();
            if (comments == null || comments.isEmpty()) {
                continue;
            }

            StringBuilder threadText = new StringBuilder();
            MessageBase firstNote = comments.get(0);

            String author = firstNote.getAuthorId();
            String body = firstNote.getBody();

            threadText.append(String.format("**Author: @%s**%n%s", author, body));

            for (int i = 1; i < comments.size(); i++) {
                MessageBase replyNote = comments.get(i);
                String replyAuthor = replyNote.getAuthorId();
                String replyBody = replyNote.getBody().trim();

                String indentedBody = replyBody.lines()
                        .map(line -> "> " + line)
                        .collect(Collectors.joining(System.lineSeparator()));

                threadText.append(String.format("%n%n**Reply from @%s:**%n%s",
                        replyAuthor, indentedBody));
            }

            discussionsText.append("---")
                    .append(System.lineSeparator())
                    .append(threadText)
                    .append(System.lineSeparator())
                    .append("---")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
        }

        return discussionsText.toString().trim();
    }
}
