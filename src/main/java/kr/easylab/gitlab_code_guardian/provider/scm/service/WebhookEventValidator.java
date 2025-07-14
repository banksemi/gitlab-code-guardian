package kr.easylab.gitlab_code_guardian.provider.scm.service;

import java.util.List;

public interface WebhookEventValidator {
    boolean validateMRComment();
    boolean validateNewThread();
    boolean validateCreated();
    boolean validateNotSelfInvolved(String botId);
    boolean validateBotMention(String botId);
    boolean validateRepository(List<String> repositoryNames);
}
