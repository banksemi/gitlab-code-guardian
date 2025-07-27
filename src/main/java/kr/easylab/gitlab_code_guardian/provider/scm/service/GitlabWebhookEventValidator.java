package kr.easylab.gitlab_code_guardian.provider.scm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.webhook.NoteEvent;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class GitlabWebhookEventValidator implements WebhookEventValidator {
    private NoteEvent getCurrentNoteEvent(NoteEvent noteEvent) {
        if (noteEvent == null) {
            throw new IllegalStateException("NoteEvent is null.");
        }
        return noteEvent;
    }

    @Override
    public boolean validateMRComment(NoteEvent noteEvent) {
        NoteEvent.NoteableType noteableType = noteEvent.getObjectAttributes().getNoteableType();
        if (noteableType == null || !noteableType.equals(NoteEvent.NoteableType.MERGE_REQUEST)) {
            log.warn("Not target NoteableType: {}", noteableType);
            return false;
        }
        return true;
    }

    @Override
    public boolean validateNewThread(NoteEvent noteEvent) {
        return true;
    }

    @Override
    public boolean validateCreated(NoteEvent noteEvent) {
        if (!noteEvent.getObjectAttributes().getCreatedAt().equals(
                noteEvent.getObjectAttributes().getUpdatedAt()
        )) {
            log.warn("Not target CreatedAt: {}", noteEvent.getObjectAttributes().getCreatedAt());
            log.warn("Not target UpdatedAt: {}", noteEvent.getObjectAttributes().getUpdatedAt());
            return false;
        }
        return true;
    }

    @Override
    public boolean validateNotSelfInvolved(NoteEvent noteEvent, String botId) {
        String user = noteEvent.getUser().getUsername();
        log.info("User: {}", user);
        if (user.equals(botId)) {
            log.warn("Bot user: {}", user);
            return false;
        }
        return true;
    }

    @Override
    public boolean validateBotMention(NoteEvent noteEvent, String botId) {
        String body = noteEvent.getObjectAttributes().getNote();
        if (!body.contains("@" + botId)) {
            log.warn("Not target user: {}", body);
            return false;
        }
        return true;
    }

    @Override
    public boolean validateRepository(NoteEvent noteEvent, List<String> repositoryNames) {
        String repositoryId = noteEvent.getProject().getPathWithNamespace();

        for (String repositoryName : repositoryNames) {
            if (repositoryId.equals(repositoryName)) {
                return true;
            }
        }
        log.warn("Not target repository: {}", repositoryId);
        return false;
    }
}
