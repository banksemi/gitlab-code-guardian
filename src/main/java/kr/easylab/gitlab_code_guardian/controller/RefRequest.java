package kr.easylab.gitlab_code_guardian.controller;

import lombok.Data;

@Data
public class RefRequest {
    private String baseRef;
    private String headRef;
}
