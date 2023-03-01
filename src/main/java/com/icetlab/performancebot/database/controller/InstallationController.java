package com.icetlab.performancebot.database.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icetlab.performancebot.database.model.GitHubRepo;
import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.service.InstallationService;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;


@Controller
public class InstallationController {

  @Autowired
  private InstallationService service;

  /**
   * <p>
   * Adds a benchmark run to the database if it exists and the payload is properly formatted.
   * <p/>
   * It should have this format:
   * 
   * <pre>{@code
   *    {
   *        "installation_id" : "314159",
   *        "repo_id": "123452456",
   *        "results": [
   *          {"benchmark": "method name"},
   *          {"benchmark": "method name 2"}
   *        ]
   *    }
   *  }</pre>
   *
   * @param payload the formatted JSON as a String
   * @return true if successful, otherwise false
   */
  public boolean addRun(String payload) {
    try {
      JsonNode node = new ObjectMapper().readTree(payload);
      String installationId = node.get("installation_id").asText();
      String repoId = node.get("repo_id").asText();
      JsonNode results = node.get("results");
      if (results.isArray()) {
        for (JsonNode methodNode : results) {
          service.addRunResultToMethod(installationId, repoId, methodNode.get("benchmark").asText(),
              methodNode.asText());
        }
      }
      return true;
    } catch (JsonProcessingException e) {
      return false;
    }
  }

  /**
   * Get all repos in an installation
   *
   * @param installationId the id of the installation
   * @return list of github repos, or empty list if the installation id doesn't exist
   */
  public List<GitHubRepo> getReposByInstallationId(String installationId) {
    try {
      return service.getReposByInstallationId(installationId);
    } catch (RuntimeException e) {
      return null;
    }
  }

  /**
   * Get a specific installation by its id
   *
   * @param installationId the id of the installation to be returned
   * @return an Installation object, or null if the installation doesn't exist
   */
  public Installation getInstallationById(String installationId) {
    try {
      return service.getInstallationById(installationId);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Adds a new installation to the database. If the id exists already in the database, the method
   * will do nothing
   *
   * @param installationId the id to the installation to be added
   * @return true if successful, otherwise false
   */
  public boolean addInstallation(String installationId) {
    try {
      service.addInstallation(installationId);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * Adds a new github repo to an existing installtion in the database. If the installation id
   * doesn't exist in the database, this method will do nothing
   *
   * @param installationId the id of the installation
   * @param repo the repo to be added
   * @return true if successful, otherwise false
   */
  public boolean addRepoToInstallation(String installationId, GitHubRepo repo) {
    try {
      service.addRepoToInstallation(installationId, repo);
      return true;
    } catch (RuntimeException e) {
      return false;
    }
  }

  /**
   * Adds a new method to an existing repo in an exsiting installation in the database If the
   * installation or the repo doesn't exist, this method will do nothing
   *
   * @param installationId
   * @param repoId
   * @param method
   * @return true if successful, otherwise false
   */
  public boolean addMethodToRepo(String installationId, String repoId, Method method) {
    try {
      service.addMethodToRepo(installationId, repoId, method);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  /**
   * Gets all methods in a repo
   * 
   * @param installationId the id of the installation
   * @param repoId the id of the repo
   * @return a set of methods, or null if the installation or repo doesn't exist
   */
  public Set<Method> getMethodsFromRepo(String installationId, String repoId) {
    try {
      return service.getMethodsFromRepo(installationId, repoId);
    } catch (NoSuchElementException e) {
      return null;
    }
  }

}
