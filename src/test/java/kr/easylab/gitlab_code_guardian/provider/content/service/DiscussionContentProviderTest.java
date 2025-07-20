package kr.easylab.gitlab_code_guardian.provider.content.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.MRDiscussion;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.MessageBase;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscussionContentProviderTest {
    @Mock
    private MRReaderService mrReaderService;

    @InjectMocks
    private DiscussionContentProvider discussionContentProvider;

    private MessageBase createMessage(String authorId, String body) {
        return MessageBase.builder()
                .authorId(authorId)
                .body(body)
                .build();
    }

    private MRDiscussion createDiscussion(MessageBase... comments) {
        return MRDiscussion.builder()
                .comments(List.of(comments))
                .build();
    }

    @Test
    void getTitle() {
        String title = discussionContentProvider.getTitle();
        assertFalse(title.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("달려있는 코멘트가 없을 때 빈 문자열을 반환")
    void getContentText_EmptyComment(List<MRDiscussion> comments) {
        when(mrReaderService.getThreads()).thenReturn(comments);
        
        String content = discussionContentProvider.getContentText();
        
        assertEquals("", content);
        verify(mrReaderService, times(1)).getThreads();
    }

    @Test
    @DisplayName("한개의 코멘트가 달려있을 때")
    void getContentText_SingleComment() {
        when(mrReaderService.getThreads()).thenReturn(
                List.of(createDiscussion(createMessage("user1", "comment")))
        );

        String content = discussionContentProvider.getContentText();
        
        String expected = "---" + System.lineSeparator() +
                "**Author: @user1**" + System.lineSeparator() +
                "comment" + System.lineSeparator() +
                "---";
        assertEquals(expected, content);
        verify(mrReaderService, times(1)).getThreads();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("한개의 Discussion이 있지만 코멘트가 없는 경우")
    void getContentText_SingleDiscussion_EmptyComment(List<MessageBase> comments) {
        when(mrReaderService.getThreads()).thenReturn(
                List.of(MRDiscussion.builder().comments(comments).build())
        );
        
        String content = discussionContentProvider.getContentText();
        
        assertEquals("", content);
        verify(mrReaderService, times(1)).getThreads();
    }

    @Test
    @DisplayName("한개의 토론에 여러개의 답글이 달렸을 때")
    void getContentText_SingleDiscussion_MultipleComments() {
        when(mrReaderService.getThreads()).thenReturn(
                List.of(createDiscussion(
                        createMessage("user1", "첫 번째 댓글"),
                        createMessage("user2", "두 번째 댓글"),
                        createMessage("user3", "세 번째 댓글")
                ))
        );

        String expected = "---" + System.lineSeparator() +
                "**Author: @user1**" + System.lineSeparator() +
                "첫 번째 댓글" + System.lineSeparator() +
                System.lineSeparator() +
                "**Reply from @user2:**" + System.lineSeparator() +
                "> 두 번째 댓글" + System.lineSeparator() +
                System.lineSeparator() +
                "**Reply from @user3:**" + System.lineSeparator() +
                "> 세 번째 댓글" + System.lineSeparator() +
                "---";
                
        String content = discussionContentProvider.getContentText();
        
        assertEquals(expected, content);
        verify(mrReaderService, times(1)).getThreads();
    }

    @Test
    @DisplayName("여러개의 토론이 달렸을 때")
    void getContentText_MultipleDiscussions() {
        when(mrReaderService.getThreads()).thenReturn(
                List.of(
                        createDiscussion(createMessage("user1", "comment")),
                        createDiscussion(createMessage("user2", "comment2"))
                )
        );

        String expected = "---" + System.lineSeparator() +
                "**Author: @user1**" + System.lineSeparator() +
                "comment" + System.lineSeparator() +
                "---" + System.lineSeparator() +
                System.lineSeparator() +
                "---" + System.lineSeparator() +
                "**Author: @user2**" + System.lineSeparator() +
                "comment2" + System.lineSeparator() +
                "---";
                
        String content = discussionContentProvider.getContentText();
        
        assertEquals(expected, content);
        verify(mrReaderService, times(1)).getThreads();
    }
}