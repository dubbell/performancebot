package com.icetlab.performancebot.github;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class GitHubAuthTest {
  ResponseEntity<String> res;
  @InjectMocks
  private GitHubAuth auth;
  @Mock
  RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {}

  @Test
  public void testGetAccessTokenFromId() {
    this.res = new ResponseEntity<>("{\"token\": \"xyz\"}", HttpStatus.OK);
    when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class)))
        .thenReturn(this.res);
    String id = "123";
    String token = "xyz";
    assertEquals(token, auth.getAccessTokenFromId(id));
  }
}
