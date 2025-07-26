package kr.easylab.gitlab_code_guardian.provider.scm.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.DiffFile;

import java.util.List;

public interface ShaFileSnapshotService {
    /** 해당 브랜치에 존재하는 모든 파일 리스트를 가져옵니다. 변경사항이 없더라도 목록에 포함됩니다. */
    List<String> getFilePaths();

    /** 선택된 파일의 모든 내용을 불러옵니다. **/
    String getFileContent(String filePath);

    /** 변경 사항을 불러옵니다. **/
    List<DiffFile> getDiff();
}
