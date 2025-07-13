package kr.easylab.gitlab_code_guardian.provider.scm.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.MergeRequestThread;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.MessageBase;

import java.util.List;

public interface MRReaderService {
    /** 현재 선택된 브랜치 이름을 반환합니다. */
    String getBranchName();

    /** MR 제목을 반환합니다. */
    String getTitle();

    /** MR 번호, 본문, 생성일, 생성자 등 기본 정보를 가져옵니다. */
    MessageBase getInfo();

    /** 해당 MR에 열린 토론(note)들을 가져옵니다.. */
    List<MergeRequestThread> getThreads();

    /** 해당 브랜치에 존재하는 모든 파일 리스트를 가져옵니다. 변경사항이 없더라도 목록에 포함됩니다. */
    List<String> getFilePaths();

    /** 선택된 파일의 모든 내용을 불러옵니다. **/
    String getFileContent(String filePath);
}
