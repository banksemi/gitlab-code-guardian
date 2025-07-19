package kr.easylab.gitlab_code_guardian.provider.content.service;

import kr.easylab.gitlab_code_guardian.llm.service.LLMService;
import kr.easylab.gitlab_code_guardian.provider.content.dto.FilePathsResponse;
import kr.easylab.gitlab_code_guardian.provider.content.service.util.RelevantFilePathFinder;
import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import kr.easylab.gitlab_code_guardian.review.service.ContentAggregator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExtendedFileContentProviderTest {
    @Mock
    private DiffContentProvider diffContentProvider;

    @Mock
    private RelevantFilePathFinder relevantFilePathFinder;

    @Mock
    private MRReaderService mrReaderService;

    @InjectMocks
    private ExtendedFileContentProvider extendedFileContentProvider;

    @Test
    void getTitle() {
        // When
        String title = extendedFileContentProvider.getTitle();

        // Then
        assertTrue(title.contains("파일 컨텍스트"));
    }

    @Test
    void getContentText() {
        // Given
        when(mrReaderService.getFilePaths()).thenReturn(List.of("Test1.java", "src/test/java/Test.java"));
        when(mrReaderService.getFileContent(eq("Test1.java"))).thenReturn("content");
        when(diffContentProvider.getContentText()).thenReturn("Diff content");
        when(relevantFilePathFinder.findRelevantFilePaths(
                List.of("Test1.java", "src/test/java/Test.java"),
                "Diff content"
        )).thenReturn(
                FilePathsResponse.builder().filePaths(List.of("Test1.java")).build()
        );
        // When
        String content = extendedFileContentProvider.getContentText();

        // Then
        assertLinesMatch("""
        **Test1.java**
        ```
        content
        ```""".lines().toList(), content.lines().toList());
    }
}