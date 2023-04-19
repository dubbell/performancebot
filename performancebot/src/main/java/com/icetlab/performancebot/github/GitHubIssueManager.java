package com.icetlab.performancebot.github;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Responsible for creating issues which are sent to the GitHub repo sending the initial payload.
 */
@Component
public class GitHubIssueManager {

  Auth auth;

  public GitHubIssueManager() {
    auth = new Auth();
  }

  /**
   * Creates an issue on the GitHub repo.
   *
   * @param endpoint the endpoint to send the issue to
   * @param title the title of the issue
   * @param message the body of the issue
   */
  public void createIssue(String endpoint, String title, String message, String id) {
    String token = auth.getAccessTokenFromId(id);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", "application/vnd.github+json");
    headers.add("X-GitHub-Api-Version", "2022-11-28");
    headers.add("Authorization", "Bearer " + token);
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("title", title);
    requestBody.put("body", message);
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
    RestTemplate restTemplate = new RestTemplate();
    try {
      restTemplate.postForEntity(endpoint, requestEntity, String.class);
    } catch (HttpClientErrorException e) {
      System.out.println(e.getResponseBodyAsString());
    }
  }
}
