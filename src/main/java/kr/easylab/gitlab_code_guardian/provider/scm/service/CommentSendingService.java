package kr.easylab.gitlab_code_guardian.provider.scm.service;

public interface CommentSendingService {
    void writeComment(String content);
    void writeComment(String filePath, Long fileLine, String content);
}
