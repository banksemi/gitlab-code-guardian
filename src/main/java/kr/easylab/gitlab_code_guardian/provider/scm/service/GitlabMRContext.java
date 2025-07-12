package kr.easylab.gitlab_code_guardian.provider.scm.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

@Service
@Scope(value="request", proxyMode= ScopedProxyMode.TARGET_CLASS)
@Getter
@Setter
public class GitlabMRContext {
}
