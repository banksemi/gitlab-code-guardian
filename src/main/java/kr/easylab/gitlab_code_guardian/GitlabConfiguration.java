package kr.easylab.gitlab_code_guardian;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.WebHookManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitlabConfiguration {
    @Bean
    GitLabApi gitlabApi(
            @Value("${gitlab.base_url}") String baseURL,
            @Value("${gitlab.token}") String token
    ) {
        return new GitLabApi(baseURL, token);
    }


}
