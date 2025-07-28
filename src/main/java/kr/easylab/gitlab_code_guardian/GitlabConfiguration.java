package kr.easylab.gitlab_code_guardian;

import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.WebHookManager;
import org.gitlab4j.api.webhook.WebHookListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class GitlabConfiguration {
    @Bean
    GitLabApi gitlabApi(
            @Value("${gitlab.base_url}") String baseURL,
            @Value("${gitlab.token}") String token
    ) {
        return new GitLabApi(baseURL, token);
    }

    @Bean
    WebHookManager webHookManager(
            @Value("${gitlab.webhook_secret}") String webhookSecret,
            List<WebHookListener> webHookListeners
    ) {
        WebHookManager webHookManager = new WebHookManager(webhookSecret);

        for (WebHookListener webHookListener : webHookListeners) {
            webHookManager.addListener(webHookListener);
            log.info("Added WebHook Listener: {}", webHookListener.getClass().getSimpleName());
        }

        return webHookManager;
    }

}
