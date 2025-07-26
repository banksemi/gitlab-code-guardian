package kr.easylab.gitlab_code_guardian.provider.content.service;

import kr.easylab.gitlab_code_guardian.provider.content.dto.FilePathsResponse;
import kr.easylab.gitlab_code_guardian.provider.content.service.util.RelevantFilePathFinder;
import kr.easylab.gitlab_code_guardian.provider.scm.service.ShaFileSnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public String getContentText() {
        // 수정 내용과 연관성 있는 파일들을 식별
        FilePathsResponse pathsResponse = relevantFilePathFinder.findRelevantFilePaths(
                shaFileSnapshotService.getFilePaths(),
                diffContentProvider.getContentText()
        );

        // 받은 경로를 기반으로 File을 불러와 Content에 포함
        StringBuilder sb = new StringBuilder();
        for (String path : pathsResponse.getFilePaths()) {
            sb.append("**").append(path).append("**").append(System.lineSeparator());
            sb.append("```").append(System.lineSeparator());
            sb.append(shaFileSnapshotService.getFileContent(path)).append(System.lineSeparator());
            sb.append("```").append(System.lineSeparator());
            sb.append(System.lineSeparator());
        }
        return sb.toString().trim();
    }
}
