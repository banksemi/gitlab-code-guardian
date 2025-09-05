package kr.easylab.gitlab_code_guardian.provider.content.service.sha;

import kr.easylab.gitlab_code_guardian.provider.content.dto.FilePathsResponse;
import kr.easylab.gitlab_code_guardian.provider.content.service.ContentProvider;
import kr.easylab.gitlab_code_guardian.provider.content.service.util.RelevantFilePathFinder;
import kr.easylab.gitlab_code_guardian.provider.scm.service.ShaFileSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExtendedFileContentProvider implements ContentProvider {
    private final ShaFileSnapshotService shaFileSnapshotService;
    private final RelevantFilePathFinder relevantFilePathFinder;
    private final DiffContentProvider diffContentProvider;

    @Override
    public String getTitle() {
        return "추가 파일 컨텍스트 (이 파일들은 정확한 리뷰를 위해 추가로 수집된 컨텍스트 입니다)";
    }

    private String fileToString(Map<String, String> fileContents) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : fileContents.entrySet()) {
            String path = entry.getKey();
            String value = entry.getValue();

            sb.append("**").append(path).append("**").append(System.lineSeparator());
            sb.append("```").append(System.lineSeparator());
            sb.append(value).append(System.lineSeparator());
            sb.append("```").append(System.lineSeparator());
            sb.append(System.lineSeparator());
        }
        return sb.toString().trim();
    }
    @Override
    public Optional<String> getContentText() {
        // 수정 내용과 연관성 있는 파일들을 식별
        Optional<String> diffText = diffContentProvider.getContentText();
        if (diffText.isEmpty())
            return Optional.empty();

        HashMap<String, String> fileContents = new HashMap<>();

        for (int i = 0; i < 10; i++) {
            FilePathsResponse pathsResponse = relevantFilePathFinder.findRelevantFilePaths(
                    shaFileSnapshotService.getFilePaths(),
                    diffText.get(),
                    "## 현재까지 읽은 파일 내용\n" + fileToString(fileContents)
            );
            if (pathsResponse.getFilePaths().isEmpty())
                break;
            for (String path : pathsResponse.getFilePaths()) {
                Optional<String> fileContent = shaFileSnapshotService.getFileContent(path);
                fileContent.ifPresent(s -> fileContents.put(path, s));
            }
        }
        return Optional.of(fileToString(fileContents));
    }
}
