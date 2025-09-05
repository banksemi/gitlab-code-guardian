package kr.easylab.gitlab_code_guardian.provider.scm.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.DiffFile;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.SCMInformation;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.RepositoryApi;
import org.gitlab4j.api.RepositoryFileApi;
import org.gitlab4j.api.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ShaFileSnapshotServiceImplTest {

    @Mock
    private SCMContext SCMContext;

    @Mock
    private GitLabApi gitLabApi;

    @Mock
    private RepositoryApi repositoryApi;

    @Mock
    private RepositoryFileApi repositoryFileApi;

    @InjectMocks
    private ShaFileSnapshotServiceImpl shaFileSnapshotService;

    private final String repositoryId = "test-repo-id";
    private final String headSha = "abc123";

    @BeforeEach
    void setUp() {
        SCMInformation scmInformation = SCMInformation.builder()
                .repositoryId(repositoryId)
                .headSha(headSha)
                .baseSha("base123")
                .build();
        
        lenient().when(SCMContext.getSCMInformation()).thenReturn(scmInformation);
        lenient().when(gitLabApi.getRepositoryApi()).thenReturn(repositoryApi);
        lenient().when(gitLabApi.getRepositoryFileApi()).thenReturn(repositoryFileApi);
    }

    @Test
    @DisplayName("getFilePaths - 빈 트리 아이템일 때 빈 리스트를 반환한다")
    void getFilePaths_whenEmptyTreeItems_shouldReturnEmptyList() throws GitLabApiException {
        // Given
        when(repositoryApi.getTree(repositoryId, "", headSha, true))
                .thenReturn(new ArrayList<>());

        // When
        List<String> result = shaFileSnapshotService.getFilePaths();

        // Then
        assertTrue(result.isEmpty());
        verify(repositoryApi).getTree(repositoryId, "", headSha, true);
    }

    @Test
    @DisplayName("getFilePaths - BLOB 타입만 필터링하여 반환한다")
    void getFilePaths_shouldFilterOnlyBlobTypes() throws GitLabApiException {
        // Given
        TreeItem blobItem1 = new TreeItem();
        blobItem1.setPath("src/main/java/Test.java");
        blobItem1.setType(TreeItem.Type.BLOB);

        TreeItem treeItem = new TreeItem();
        treeItem.setPath("src/main/java");
        treeItem.setType(TreeItem.Type.TREE);

        TreeItem blobItem2 = new TreeItem();
        blobItem2.setPath("README.md");
        blobItem2.setType(TreeItem.Type.BLOB);

        List<TreeItem> treeItems = List.of(blobItem1, treeItem, blobItem2);
        when(repositoryApi.getTree(repositoryId, "", headSha, true))
                .thenReturn(treeItems);

        // When
        List<String> result = shaFileSnapshotService.getFilePaths();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains("src/main/java/Test.java"));
        assertTrue(result.contains("README.md"));
        assertFalse(result.contains("src/main/java"));
    }

    @Test
    @DisplayName("getFilePaths - GitLabApiException 발생 시 RuntimeException을 던진다")
    void getFilePaths_whenGitLabApiException_shouldThrowRuntimeException() throws GitLabApiException {
        // Given
        when(repositoryApi.getTree(repositoryId, "", headSha, true))
                .thenThrow(new GitLabApiException("API Error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> shaFileSnapshotService.getFilePaths());
    }

    @Test
    @DisplayName("getFileContent - 정상적인 파일 내용을 반환한다")
    void getFileContent_shouldReturnFileContent() throws GitLabApiException {
        // Given
        String filePath = "src/main/java/Test.java";
        String expectedContent = "public class Test {}";
        
        RepositoryFile repositoryFile = new RepositoryFile();
        repositoryFile.setContent(expectedContent);
        
        when(repositoryFileApi.getFile(repositoryId, filePath, headSha))
                .thenReturn(repositoryFile);

        // When
        String result = shaFileSnapshotService.getFileContent(filePath).get();

        // Then
        assertEquals(expectedContent, result);
        verify(repositoryFileApi).getFile(repositoryId, filePath, headSha);
    }

    @Test
    @DisplayName("getFileContent - GitLabApiException 발생 시 Optional.empty를 반환한다")
    void getFileContent_whenGitLabApiException_shouldReturnNull() throws GitLabApiException {
        // Given
        String filePath = "src/main/java/Test.java";
        when(repositoryFileApi.getFile(repositoryId, filePath, headSha))
                .thenThrow(new GitLabApiException("File not found"));

        // When
        Optional<String> result = shaFileSnapshotService.getFileContent(filePath);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getDiff - 정상적인 diff 결과를 반환한다")
    void getDiff_shouldReturnDiffFiles() throws GitLabApiException {
        // Given
        Diff diff1 = new Diff();
        diff1.setNewPath("file1.java");
        diff1.setOldPath("file1_old.java");
        diff1.setDiff("diff content 1");
        diff1.setNewFile(true);

        Diff diff2 = new Diff();
        diff2.setNewPath("file2.java");
        diff2.setDiff("diff content 2");
        diff2.setDeletedFile(true);

        CompareResults compareResults = new CompareResults();
        compareResults.setDiffs(List.of(diff1, diff2));

        when(repositoryApi.compare(repositoryId, "base123", "abc123", null, false))
                .thenReturn(compareResults);

        // When
        List<DiffFile> result = shaFileSnapshotService.getDiff();

        // Then
        assertEquals(2, result.size());
        
        DiffFile resultDiff1 = result.get(0);
        assertEquals("file1.java", resultDiff1.getNewPath());
        assertEquals("file1_old.java", resultDiff1.getOldPath());
        assertEquals("diff content 1", resultDiff1.getDiff());
        assertTrue(resultDiff1.getCreated());

        DiffFile resultDiff2 = result.get(1);
        assertEquals("file2.java", resultDiff2.getNewPath());
        assertEquals("diff content 2", resultDiff2.getDiff());
        assertTrue(resultDiff2.getDeleted());

        verify(repositoryApi).compare(repositoryId, "base123", "abc123", null, false);
    }

    @Test
    @DisplayName("getDiff - GitLabApiException 발생 시 IllegalStateException을 던진다")
    void getDiff_whenGitLabApiException_shouldThrowIllegalStateException() throws GitLabApiException {
        // Given
        when(repositoryApi.compare(repositoryId, "base123", "abc123", null, false))
                .thenThrow(new GitLabApiException("Compare failed"));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
                () -> shaFileSnapshotService.getDiff());
        
        assertEquals("Diff 정보를 가져오는 데 실패했습니다.", exception.getMessage());
        verify(repositoryApi).compare(repositoryId, "base123", "abc123", null, false);
    }
}