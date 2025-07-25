package kr.easylab.gitlab_code_guardian.prompt;

import org.springframework.stereotype.Service;

@Service
public class RelevantFilePathFinderPrompt {
    public String getPrompt() {
        return """
            당신은 AI 리뷰를 위해 컨텍스트 정보를 수집해야합니다.
            리뷰를 위해 추가로 필요한 파일들의 정확한 경로를 반환하세요.
    
            입력되는 데이터
            - 코드 변경사항
            - 파일 맵
    
            출력 데이터
            - List: 파일의 정확한 경로 (파일 맵에 명시된 경로 사용)
    
            고려할 수 있는 정보들
            - 수정된 파일 (전체 내용 확인을 위함)
            - 변경된 코드와 연결된 **구현체(implementation)**
            - 호출되거나 의존하는 **인터페이스(interface)** 정의
            - 사용 흐름이나 동작을 보여주는 다른 예시 파일들
            - 유닛 테스트나 관련 설정 등
            - 단 바이너리 데이터는 읽을 수 없습니다.
            """;
    }
}
