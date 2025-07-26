package kr.easylab.gitlab_code_guardian.provider.scm.service;

import kr.easylab.gitlab_code_guardian.provider.scm.dto.DiffFile;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.MRDiscussion;
import kr.easylab.gitlab_code_guardian.provider.scm.dto.MessageBase;

import java.util.List;

public interface MRReaderService {
    Boolean isAvailable();

    /** MR 제목을 반환합니다. */
    String getTitle();

    /** MR 번호, 본문, 생성일, 생성자 등 기본 정보를 가져옵니다. */
    MessageBase getInfo();

    /** 해당 MR에 열린 토론(note)들을 가져옵니다.. */
    List<MRDiscussion> getThreads();
}
