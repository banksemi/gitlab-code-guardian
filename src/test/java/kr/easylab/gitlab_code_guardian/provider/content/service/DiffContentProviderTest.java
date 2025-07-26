package kr.easylab.gitlab_code_guardian.provider.content.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.DiffFile;
import kr.easylab.gitlab_code_guardian.provider.scm.service.ShaFileSnapshotService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiffContentProviderTest {

    @Test
    @DisplayName("getTitle 메서드가 'Diff'를 반환하는지 확인합니다.")
    void getTitle_shouldReturnDiff() {
        // Given
        ShaFileSnapshotService mockShaFileSnapshotService = mock(ShaFileSnapshotService.class);
        DiffContentProvider diffContentProvider = new DiffContentProvider(mockShaFileSnapshotService);

        // When
        String title = diffContentProvider.getTitle();

        // Then
        assertEquals("Diff", title);
    }

    @Test
    @DisplayName("diff 파일이 없을 때 빈 문자열이 반환되는지 확인합니다.")
    void getContentText_whenNoDiffFiles_shouldReturnEmptyString() {
        // Given
        ShaFileSnapshotService mockShaFileSnapshotService = mock(ShaFileSnapshotService.class);
        when(mockShaFileSnapshotService.getDiff()).thenReturn(new ArrayList<>());

        DiffContentProvider diffContentProvider = new DiffContentProvider(mockShaFileSnapshotService);

        // When
        String content = diffContentProvider.getContentText();

        // Then
        assertEquals("", content);
        verify(mockShaFileSnapshotService).getDiff();
    }

    @Test
    @DisplayName("newPath가 있는 diff 파일의 내용이 올바르게 포맷되는지 확인합니다.")
    void getContentText_withNewPath_shouldFormatCorrectly() {
        // Given
        DiffFile diffFile = DiffFile.builder()
                .newPath("src/main/java/Test.java")
                .oldPath("src/main/java/OldTest.java")
                .diff("123123")
                .build();

        ShaFileSnapshotService mockShaFileSnapshotService = mock(ShaFileSnapshotService.class);
        when(mockShaFileSnapshotService.getDiff()).thenReturn(List.of(diffFile));

        DiffContentProvider diffContentProvider = new DiffContentProvider(mockShaFileSnapshotService);

        // When
        String content = diffContentProvider.getContentText();

        // Then
        String expected = "--- File: src/main/java/Test.java ---" + System.lineSeparator() +
                         "123123" + System.lineSeparator() +
                         System.lineSeparator();
        assertEquals(expected, content);
        verify(mockShaFileSnapshotService).getDiff();
    }

    @Test
    @DisplayName("newPath가 null이거나 빈 문자열일 때 oldPath를 사용하는지 확인합니다.")
    void getContentText_whenNewPathIsNullOrEmpty_shouldUseOldPath() {
        // Given
        DiffFile diffFile1 = DiffFile.builder()
                .newPath(null)
                .oldPath("src/main/java/DeletedFile.java")
                .diff("deleted file content")
                .build();

        DiffFile diffFile2 = DiffFile.builder()
                .newPath("")
                .oldPath("src/main/java/AnotherDeletedFile.java")
                .diff("another deleted file content")
                .build();

        ShaFileSnapshotService mockShaFileSnapshotService = mock(ShaFileSnapshotService.class);
        when(mockShaFileSnapshotService.getDiff()).thenReturn(List.of(diffFile1, diffFile2));

        DiffContentProvider diffContentProvider = new DiffContentProvider(mockShaFileSnapshotService);

        // When
        String content = diffContentProvider.getContentText();

        // Then
        assertTrue(content.contains("--- File: src/main/java/DeletedFile.java ---"));
        assertTrue(content.contains("--- File: src/main/java/AnotherDeletedFile.java ---"));
        assertTrue(content.contains("deleted file content"));
        assertTrue(content.contains("another deleted file content"));
        verify(mockShaFileSnapshotService).getDiff();
    }

    @Test
    @DisplayName("여러 diff 파일이 있을 때 모든 파일이 올바르게 포맷되는지 확인합니다.")
    void getContentText_withMultipleDiffFiles_shouldFormatAllFiles() {
        // Given
        DiffFile diffFile1 = DiffFile.builder()
                .newPath("file1.java")
                .diff("diff content 1")
                .build();

        DiffFile diffFile2 = DiffFile.builder()
                .newPath("file2.java")
                .diff("diff content 2")
                .build();

        ShaFileSnapshotService mockShaFileSnapshotService = mock(ShaFileSnapshotService.class);
        when(mockShaFileSnapshotService.getDiff()).thenReturn(List.of(diffFile1, diffFile2));

        DiffContentProvider diffContentProvider = new DiffContentProvider(mockShaFileSnapshotService);

        // When
        String content = diffContentProvider.getContentText();

        // Then
        assertTrue(content.contains("--- File: file1.java ---"));
        assertTrue(content.contains("--- File: file2.java ---"));
        assertTrue(content.contains("diff content 1"));
        assertTrue(content.contains("diff content 2"));

        String[] parts = content.split(System.lineSeparator() + System.lineSeparator());
        assertEquals(2, parts.length);
        verify(mockShaFileSnapshotService).getDiff();
    }
}
