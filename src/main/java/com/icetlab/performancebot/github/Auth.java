package com.icetlab.performancebot.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


/**
 * Authorizes and authenticates the bot to access the target repository.
 */
public class Auth {

  private final String appId;
  private final String privateKeyPath;
  private String jwt;
  private Date expiresAt;
  private Map<String, String> installationIds;

  /**
   * Creates a new Auth object.
   */
  public Auth() {
    installationIds = new HashMap<>();
    java.security.Security.addProvider(
        new org.bouncycastle.jce.provider.BouncyCastleProvider()
    );
    GitHubConfig gitHubConfig = new AnnotationConfigApplicationContext(GitHubConfig.class)
        .getBean(GitHubConfig.class);
    appId = gitHubConfig.getAppId();
    privateKeyPath = gitHubConfig.getPrivateKeyPath();
    try { // TODO: Not sure if good practice
      fetchAndPopulateInstallationIds();
    }
    catch (Throwable e){
      System.out.println("Bad access key!");
    }
    jwt = createWebToken();
  }

  /**
   * Fetches the installation IDs and their corresponding access token URLs from the GitHub API and
   * returns them as a Map.
   */
  private void fetchAndPopulateInstallationIds() {
    HttpHeaders headers = createHeaders();

    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<String> request = new HttpEntity<>(headers);
    ResponseEntity<String> response = restTemplate
            .exchange("https://api.github.com/app/installations", HttpMethod.GET, request,
                    String.class);

    validateResponse(response);

    JsonNode jsonNode = convertResponseToJSONNode(response.getBody());
    populateInstallationIds(jsonNode);

  }

  /**
   * Returns the headers required to make a request to the GitHub API.
   */
  private HttpHeaders createHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", "application/vnd.github+json");
    headers.set("Authorization", "Bearer " + getJwt());
    return headers;
  }

  /**
   * Makes sure the response is valid, throws an exception if it is not.
   */
  private void validateResponse(ResponseEntity<String> response) {
    if (response.getStatusCode() != HttpStatus.OK) {
      throw new HttpClientErrorException(response.getStatusCode());
    }
  }

  /**
   * Returns a json string as a JsonNode.
   */
  private JsonNode convertResponseToJSONNode(String jsonString) {
    try {
      return new ObjectMapper().readTree(jsonString);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Populates the installationIds map with the installation IDs and their corresponding access
   * token.
   *
   * @param jsonNode The JsonNode to populate the map from
   */
  private void populateInstallationIds(JsonNode jsonNode) {
    String id, url;
    for (JsonNode node : jsonNode) {
      id = node.get("target_id").asText();
      url = node.get("access_tokens_url").asText();
      installationIds.put(id, url);
    }
  }

  /**
   * Creates a JSON Web Token (JWT) for the GitHub App.
   *
   * @return The JWT as a String
   */
  private String createWebToken() {
    Date now = new Date(System.currentTimeMillis());
    expiresAt = getTimeIn10Minutes();
    return Jwts.builder().setIssuedAt(now)
        .setExpiration(expiresAt)
        .setIssuer(appId)
        .signWith(generatePrivateKey(), SignatureAlgorithm.RS256).compact();
  }

  /**
   * Retrieves an access token from the GitHub API for a specific installation identified by the
   * given ID.
   *
   * @param id The ID of the GitHub installation to retrieve the access token for
   * @return The access token as a String
   */
  public String getAccessTokenFromId(String id) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Accept", "application/vnd.github+json");
    headers.set("Authorization", "Bearer " + getJwt());

    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<String> request = new HttpEntity<>(headers);
    ResponseEntity<String> res = restTemplate
        .exchange(
            String.format("https://api.github.com/app/installations/%s/access_tokens", id.trim()),
            HttpMethod.POST, request,
            String.class);
    return (String) new JacksonJsonParser().parseMap(res.getBody()).get("token");
  }

  /**
   * Generates a JWT if not already generated.
   *
   * @return the JWT as a String
   */
  private String getJwt() {
    if (expiresAt == null || expiresAt.after(new Date(System.currentTimeMillis()))) {
      jwt = createWebToken();
    }
    return jwt;
  }

  /**
   * Generates a private key from the private key PEM file specified in `application.properties`.
   *
   * @return PrivateKey
   */
  private PrivateKey generatePrivateKey() {
    String key = null;
    try {
      key = Files.readString(Paths.get(privateKeyPath),
          Charset.defaultCharset());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    String privateKey = key.replace("-----BEGIN RSA PRIVATE KEY-----", "")
        .replaceAll(System.lineSeparator(), "").replace("-----END RSA PRIVATE KEY-----", "");

    byte[] encoded = Base64.getDecoder().decode(privateKey);
    KeyFactory kf = null;
    try {
      kf = KeyFactory.getInstance("RSA");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);

    try {
      return kf.generatePrivate(keySpec);
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns a date object that is 10 minutes from now.
   *
   * @return Date ten minutes from now.
   */
  private Date getTimeIn10Minutes() {
    return new Date(System.currentTimeMillis() + 10 * 60 * 1000);
  }
}
