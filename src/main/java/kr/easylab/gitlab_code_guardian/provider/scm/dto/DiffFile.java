package kr.easylab.gitlab_code_guardian.provider.scm.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiffFile {
    /** 변경 전 파일 권한 */
    private final String a_mode;
    /** 변경 후 파일 권한 */
    private final String b_mode;


    /** 변경 내용 */
    private final String diff;

    /** 변경 후 파일 경로 */
    private final String newPath;

    /** 변경 전 파일 경로 */
    private final String oldPath;


    /** 파일이 이동되었는지 여부 */
    private final Boolean renamed;

    /** 파일이 생성되었는지 여부 */
    private final Boolean created;

    /** 파일이 삭제되었는지 여부 */
    private final Boolean deleted;

}
