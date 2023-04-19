package com.icetlab.performancebot.github;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SpringBootTest
public class GitHubIssueManagerTest {

  private GitHubIssueManager issue;
  private GitHubAuth auth;
  private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    issue = spy(GitHubIssueManager.getInstance());
    auth = mock(GitHubAuth.class);
    issue.auth = auth;
    restTemplate = mock(RestTemplate.class);
  }

  @Test
  public void testCreateIssue() {
    String endpoint = "https://api.github.com/repos/iceT-Lab/performance-bot/issues";
    String title = "Test title";
    String message = "Test message";
    String id = "123";
    String token = "xyz";
    ResponseEntity<Object> res = new ResponseEntity<>("{\"token\": \"xyz\"}", HttpStatus.OK);
    when(auth.getAccessTokenFromId(id)).thenReturn(token);
    when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(res);
    issue.createIssue(endpoint, title, message, id);
    verify(issue, times(1)).createIssue(endpoint, title, message, id);
  }
}
