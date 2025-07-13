package kr.easylab.gitlab_code_guardian.review.controller;

import kr.easylab.gitlab_code_guardian.provider.scm.service.GitlabMRContext;
import kr.easylab.gitlab_code_guardian.review.dto.MRReview;
import kr.easylab.gitlab_code_guardian.review.service.MergeRequestPipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Controller
@Slf4j
public class TestController {
    private final GitlabMRContext gitlabMRContext;
    private final MergeRequestPipeline mergeRequestPipeline;

    @GetMapping("/test")
    @ResponseBody
    public MRReview test() {
        gitlabMRContext.setMrId(15L);
        gitlabMRContext.setRepositoryId("test");
        return mergeRequestPipeline.runAndNotify();
    }
}
