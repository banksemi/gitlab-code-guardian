package kr.easylab.gitlab_code_guardian.review.service;

import kr.easylab.gitlab_code_guardian.provider.scm.service.MRReaderService;
import kr.easylab.gitlab_code_guardian.review.dto.MRReview;

public interface MergeRequestReviewService {
    MRReview review(MRReaderService mrReaderService);
}
