package kr.easylab.gitlab_code_guardian.review.service;

import kr.easylab.gitlab_code_guardian.provider.scm.service.WebhookEventValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.webhook.NoteEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReviewConditionChecker {
    private final String botId;
    private final String repositoryName;
    private final WebhookEventValidator webhookEventValidator;

    public ReviewConditionChecker(
            WebhookEventValidator webhookEventValidator,
            @Value("${gitlab.bot_id}") String botId,
            @Value("${gitlab.repository}") String repositoryName
    ) {
        this.botId = botId;
        this.webhookEventValidator = webhookEventValidator;
        this.repositoryName = repositoryName;
    }

    public boolean isAllowed(NoteEvent noteEvent) {
        if (!webhookEventValidator.validateMRComment(noteEvent)) {
            log.warn("Not MR Comment");
            return false;
        }

        if (!webhookEventValidator.validateNewThread(noteEvent)) {
            log.warn("Not New Thread");
            return false;
        }

        if (!webhookEventValidator.validateCreated(noteEvent)) {
            log.warn("Not Created Event");
            return false;
        }

        if (!webhookEventValidator.validateNotSelfInvolved(noteEvent, botId)) {
            log.warn("Self Involved");
            return false;
        }

        if (!webhookEventValidator.validateBotMention(noteEvent, botId)) {
            log.warn("Not Bot Mention");
            return false;
        }

        if (!webhookEventValidator.validateRepository(noteEvent, List.of(repositoryName))) {
            log.warn("Not Target Repository");
            return false;
        }

        return true;
    }
}
