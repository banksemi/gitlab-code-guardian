package kr.easylab.gitlab_code_guardian.provider.scm.service;

import org.gitlab4j.api.webhook.NoteEvent;

import java.util.List;

public interface WebhookEventValidator {
    boolean validateMRComment(NoteEvent noteEvent);
    boolean validateNewThread(NoteEvent noteEvent);
    boolean validateCreated(NoteEvent noteEvent);
    boolean validateNotSelfInvolved(NoteEvent noteEvent, String botId);
    boolean validateBotMention(NoteEvent noteEvent, String botId);
    boolean validateRepository(NoteEvent noteEvent, List<String> repositoryNames);
}
