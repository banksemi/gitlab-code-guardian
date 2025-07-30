package kr.easylab.gitlab_code_guardian.provider.content.service.util;

import kr.easylab.gitlab_code_guardian.llm.dto.LLMConfig;
import kr.easylab.gitlab_code_guardian.llm.dto.LLMMessage;
import kr.easylab.gitlab_code_guardian.llm.service.LLMService;
import kr.easylab.gitlab_code_guardian.prompt.RelevantFilePathFinderPrompt;
import kr.easylab.gitlab_code_guardian.provider.content.dto.FilePathsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LLMBasedRelevantFilePathFinder implements RelevantFilePathFinder {
    private final LLMService llmService;
    private final RelevantFilePathFinderPrompt prompt;

    @Override
    public FilePathsResponse findRelevantFilePaths(List<String> filePaths, String diffContent) {
        FilePathsResponse pathsResponse = llmService.generate(
                List.of(
                        LLMMessage.builder()
                                .role(LLMMessage.Role.USER)
                                .text("## 코드 변경사항" + System.lineSeparator() + diffContent)
                                .build(),
                        LLMMessage.builder()
                                .role(LLMMessage.Role.USER)
                                .text("## 파일 맵" + System.lineSeparator() + String.join("\n", filePaths))
                                .build()
                ),
                FilePathsResponse.class
                , LLMConfig.builder()
                        .prompt(prompt.getPrompt())
                                .thinkingBudget(512L)
                        .build()

        );
        if (pathsResponse == null || pathsResponse.getFilePaths() == null || pathsResponse.getFilePaths().isEmpty())
            return new FilePathsResponse(List.of());
        return pathsResponse;
    }
}
