package kr.easylab.gitlab_code_guardian.review.service;

import kr.easylab.gitlab_code_guardian.llm.dto.LLMMessage;
import kr.easylab.gitlab_code_guardian.provider.content.service.ContentProvider;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContentAggregatorImplTest {
    @Test
    @DisplayName("컨텐츠가 없을 때 빈 배열이 반환되는지 확인합니다.")
    void aggregate_whenContentIsEmpty_shouldReturnEmptyList() {
        // Given
        ContentAggregatorImpl contentAggregator = new ContentAggregatorImpl(
                new ArrayList<>()
        );

        // When
        List<LLMMessage> messages = contentAggregator.aggregate();

        // Then
        assertNotNull(messages);
        assertEquals(0, messages.size());
    }

    @Test
    @DisplayName("컨텐츠가 있는 경우 적절한 메세지가 반환되는지 확인합니다.")
    void aggregate_whenContentIsNotEmpty_shouldReturnReviewMessage() {
        // Given
        ContentProvider mockContentProvider1 = mock(ContentProvider.class);
        ContentProvider mockContentProvider2 = mock(ContentProvider.class);

        when(mockContentProvider1.getTitle()).thenReturn("Title 1");
        when(mockContentProvider1.getContentText()).thenReturn(Optional.of("Content 1"));

        when(mockContentProvider2.getTitle()).thenReturn("Title 2");
        when(mockContentProvider2.getContentText()).thenReturn(Optional.of("Content 2"));

        List<ContentProvider> contentProviders = List.of(mockContentProvider1, mockContentProvider2);
        ContentAggregatorImpl contentAggregator = new ContentAggregatorImpl(contentProviders);

        // When
        List<LLMMessage> messages = contentAggregator.aggregate();

        // Then
        assertNotNull(messages);
        assertEquals(2, messages.size());

        // Verify first message
        LLMMessage firstMessage = messages.get(0);
        assertEquals(LLMMessage.Role.USER, firstMessage.getRole());
        assertEquals("## Title 1\nContent 1", firstMessage.getText());

        // Verify second message
        LLMMessage secondMessage = messages.get(1);
        assertEquals(LLMMessage.Role.USER, secondMessage.getRole());
        assertEquals("## Title 2\nContent 2", secondMessage.getText());

        // Verify that methods were called
        verify(mockContentProvider1).getTitle();
        verify(mockContentProvider1).getContentText();
        verify(mockContentProvider2).getTitle();
        verify(mockContentProvider2).getContentText();
    }

    @Test
    @DisplayName("일부 컨텐츠가 null이거나 빈 문자열인 경우 해당 컨텐츠는 제외되는지 확인합니다.")
    void aggregate_whenSomeContentIsNullOrEmpty_shouldSkipEmptyContent() {
        // Given
        ContentProvider mockContentProvider1 = mock(ContentProvider.class);
        ContentProvider mockContentProvider2 = mock(ContentProvider.class);
        ContentProvider mockContentProvider3 = mock(ContentProvider.class);
        MRReaderService mockMRReaderService = mock(MRReaderService.class);

        when(mockContentProvider1.getTitle()).thenReturn("Title 1");
        when(mockContentProvider1.getContentText()).thenReturn(Optional.of("Content 1"));

        when(mockContentProvider2.getTitle()).thenReturn("Title 2");
        when(mockContentProvider2.getContentText()).thenReturn(Optional.empty());

        when(mockContentProvider3.getTitle()).thenReturn("Title 3");
        when(mockContentProvider3.getContentText()).thenReturn(Optional.of(""));

        List<ContentProvider> contentProviders = List.of(mockContentProvider1, mockContentProvider2, mockContentProvider3);
        ContentAggregatorImpl contentAggregator = new ContentAggregatorImpl(contentProviders);

        // When
        List<LLMMessage> messages = contentAggregator.aggregate();

        // Then
        assertNotNull(messages);
        assertEquals(1, messages.size());

        LLMMessage message = messages.get(0);
        assertEquals(LLMMessage.Role.USER, message.getRole());
        assertEquals("## Title 1\nContent 1", message.getText());
    }
}
