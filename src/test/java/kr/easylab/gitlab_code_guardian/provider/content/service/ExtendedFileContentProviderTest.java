package kr.easylab.gitlab_code_guardian.provider.content.service;

import kr.easylab.gitlab_code_guardian.provider.content.dto.FilePathsResponse;
import kr.easylab.gitlab_code_guardian.provider.content.service.sha.DiffContentProvider;
import kr.easylab.gitlab_code_guardian.provider.content.service.sha.ExtendedFileContentProvider;
import kr.easylab.gitlab_code_guardian.provider.content.service.util.RelevantFilePathFinder;
import kr.easylab.gitlab_code_guardian.provider.scm.service.ShaFileSnapshotService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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
    private ShaFileSnapshotService shaFileSnapshotService;

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
        when(shaFileSnapshotService.getFilePaths()).thenReturn(List.of("Test1.java", "src/test/java/Test.java"));
        when(shaFileSnapshotService.getFileContent(eq("Test1.java"))).thenReturn(Optional.of("content"));
        when(diffContentProvider.getContentText()).thenReturn(Optional.of("Diff content"));

        when(relevantFilePathFinder.findRelevantFilePaths(
                eq(List.of("Test1.java", "src/test/java/Test.java")),
                eq("Diff content"),
                eq("## 현재까지 읽은 파일 내용\n")
        )).thenReturn(
                FilePathsResponse.builder().filePaths(List.of("Test1.java")).build()
        );

        when(relevantFilePathFinder.findRelevantFilePaths(
                eq(List.of("Test1.java", "src/test/java/Test.java")),
                eq("Diff content"),
                eq("## 현재까지 읽은 파일 내용\n**Test1.java**" + System.lineSeparator() +
                   "```" + System.lineSeparator() +
                   "content" + System.lineSeparator() +
                   "```")
        )).thenReturn(
                FilePathsResponse.builder().filePaths(List.of()).build()
        );

        // When
        String content = extendedFileContentProvider.getContentText().orElse("");

        // Then
        assertLinesMatch("""
        **Test1.java**
        ```
        content
        ```""".lines().toList(), content.lines().toList());
    }
}