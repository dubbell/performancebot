package com.icetlab.performancebot.stats;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class ImageKitUploader {
  /**
   * Uploads an image to imagekit and returns the imageUrl if successful.
   *
   * @throws IOException if prviate key cannot be loaded
   */
  static String uploadImage(String fileName, String base64Img) throws IOException {
    HttpHeaders headers = generateHeaders();
    MultiValueMap<String, Object> body = generateRequestBodyFromFile(fileName, base64Img);
    ResponseEntity<String> response = postToImageKit(body, headers);
    String imageUrl = getUrlFromResponse(response.getBody());
    return imageUrl;
  }

  /**
   * Generates headers for sending POST request to imagekit upload file endpoint
   *
   * @return HttpHeaders
   * @throws IOException if private key cannot be loaded from properties file
   */
  static private HttpHeaders generateHeaders() throws IOException {
    HttpHeaders headers = new HttpHeaders();
    String encodedPrivateKey = Base64.getEncoder().encodeToString(readKeyFromProps().getBytes());
    headers.setBasicAuth(encodedPrivateKey);
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    return headers;
  }

  /**
   * @param base64Img the image (encoded as base64 string) to be uploaded to imagekit
   * @param fileName the name of the file when uploaded to imagekit
   */
  static private MultiValueMap<String, Object> generateRequestBodyFromFile(String fileName,
      String base64Img) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", base64Img);
    body.add("fileName", fileName);
    return body;
  }

  static private ResponseEntity<String> postToImageKit(MultiValueMap<String, Object> body,
      HttpHeaders headers) {
    String endpoint = "https://upload.imagekit.io/api/v1/files/upload/";
    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response =
        restTemplate.postForEntity(endpoint, requestEntity, String.class);
    return response;
  }

  static private String getUrlFromResponse(String body)
      throws JsonMappingException, JsonProcessingException {
    JsonNode node = new ObjectMapper().readTree(body);
    String url = node.get("url").asText();
    return url;
  }

  static private String readKeyFromProps() throws IOException {
    Properties prop = new Properties();
    FileInputStream properties = new FileInputStream("src/main/resources/application.properties");
    prop.load(properties);
    String privateKey = prop.getProperty("imagekit.privatekey");
    return privateKey;
  }
}
