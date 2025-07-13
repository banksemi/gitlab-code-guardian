package kr.easylab.gitlab_code_guardian.provider.content.service;

import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileMapContentProviderTest {

    @Test
    void getTitle() {
        // Given
        MRReaderService mockMRReaderService = mock(MRReaderService.class);
        FileMapContentProvider fileMapContentProvider = new FileMapContentProvider(mockMRReaderService);

        // When
        String title = fileMapContentProvider.getTitle();

        // Then
        assertEquals("FileMap (모든 파일 목록)", title);
    }

    @Test
    @DisplayName("파일 경로가 없을 때 빈 문자열이 반환되는지 확인합니다.")
    void getContentText_whenNoFilePaths_shouldReturnEmptyString() {
        // Given
        MRReaderService mockMRReaderService = mock(MRReaderService.class);
        when(mockMRReaderService.getFilePaths()).thenReturn(new ArrayList<>());

        FileMapContentProvider fileMapContentProvider = new FileMapContentProvider(mockMRReaderService);

        // When
        String content = fileMapContentProvider.getContentText();

        // Then
        assertEquals("", content);
        verify(mockMRReaderService).getFilePaths();
    }

    @Test
    @DisplayName("단일 파일 경로가 있을 때 올바르게 반환되는지 확인합니다.")
    void getContentText_withSingleFilePath_shouldReturnCorrectly() {
        // Given
        List<String> filePaths = List.of("src/main/java/Test.java");
        MRReaderService mockMRReaderService = mock(MRReaderService.class);
        when(mockMRReaderService.getFilePaths()).thenReturn(filePaths);

        FileMapContentProvider fileMapContentProvider = new FileMapContentProvider(mockMRReaderService);

        // When
        String content = fileMapContentProvider.getContentText();

        // Then
        assertEquals("src/main/java/Test.java", content);
        verify(mockMRReaderService).getFilePaths();
    }

    @Test
    @DisplayName("여러 파일 경로가 있을 때 개행문자로 구분되어 반환되는지 확인합니다.")
    void getContentText_withMultipleFilePaths_shouldJoinWithNewlines() {
        // Given
        List<String> filePaths = List.of(
                "src/main/java/File1.java",
                "src/main/java/File2.java",
                "src/test/java/TestFile.java"
        );
        MRReaderService mockMRReaderService = mock(MRReaderService.class);
        when(mockMRReaderService.getFilePaths()).thenReturn(filePaths);

        FileMapContentProvider fileMapContentProvider = new FileMapContentProvider(mockMRReaderService);

        // When
        String content = fileMapContentProvider.getContentText();

        // Then
        String expected = "src/main/java/File1.java\nsrc/main/java/File2.java\nsrc/test/java/TestFile.java";
        assertEquals(expected, content);
        verify(mockMRReaderService).getFilePaths();
    }

}