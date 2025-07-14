package kr.easylab.gitlab_code_guardian.review.service;

import kr.easylab.gitlab_code_guardian.provider.scm.service.WebhookEventValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public boolean isAllowed() {
        if (!webhookEventValidator.validateMRComment()) {
            log.warn("Not MR Comment");
            return false;
        }

        if (!webhookEventValidator.validateNewThread()) {
            log.warn("Not New Thread");
            return false;
        }

        if (!webhookEventValidator.validateCreated()) {
            log.warn("Not Created Event");
            return false;
        }

        if (!webhookEventValidator.validateNotSelfInvolved(botId)) {
            log.warn("Self Involved");
            return false;
        }

        if (!webhookEventValidator.validateBotMention(botId)) {
            log.warn("Not Bot Mention");
            return false;
        }

        if (!webhookEventValidator.validateRepository(List.of(repositoryName))) {
            log.warn("Not Target Repository");
            return false;
        }

        return true;
    }
}
