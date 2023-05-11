package com.icetlab.performancebot.database.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icetlab.performancebot.database.model.GitHubRepo;
import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.model.InstallationCollectionException;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.service.InstallationService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
   *        "name: "",
   *        "methods": [
   *          {"benchmark": "method name"},
   *          {"benchmark": "method name 2"}
   *        ]
   *    }
   *  }</pre>
   *
   * @param payloadFromBenchmarkWorker the formatted JSON as a String
   * @return true if successful, otherwise false
   */
  public boolean addRun(String payloadFromBenchmarkWorker) {
    try {
      JsonNode node = new ObjectMapper().readTree(payloadFromBenchmarkWorker);
      String installationId = node.get("installation_id").asText();
      String repoId = node.get("repo_id").asText();
      JsonNode results = node.get("results");
      Installation inst = getInstallationById(installationId);

      // check if installation exists, otherwise add it
      if (inst == null) {
        addInstallation(installationId);
        inst = getInstallationById(installationId);
      }

      // check if repo exists, otherwise add it
      boolean repoExists = false;
      for (GitHubRepo repo : inst.getRepos()) {
        if (repo.getRepoId().equals(repoId)) {
          repoExists = true;
          break;
        }
      }

      if (!repoExists) {
        addRepoToInstallation(installationId,
            createGitHubRepoFromPayload(payloadFromBenchmarkWorker));
      }

      boolean methodExists;
      if (results.isArray()) {
        for (JsonNode methodNode : results) {
          methodExists = service.getMethodsFromRepo(installationId, repoId).stream().anyMatch(
              method -> method.getMethodName().equals(methodNode.get("benchmark").asText()));
          if (!methodExists) {
            addMethodToRepo(installationId, repoId,
                new Method(methodNode.get("benchmark").asText(), new ArrayList<>()));
          }
          service.addRunResultToMethod(installationId, repoId, methodNode.get("benchmark").asText(),
              methodNode.toString());
        }
      }
      return true;
    } catch (JsonProcessingException | InstallationCollectionException e) {
      return false;
    }
  }

  private GitHubRepo createGitHubRepoFromPayload(String payload) {
    try {
      JsonNode node = new ObjectMapper().readTree(payload);
      String repoId = node.get("repo_id").asText();
      String name = node.get("name").asText();
      return new GitHubRepo(repoId, new HashSet<>(), name);
    } catch (JsonProcessingException e) {
      return null;
    }
  }

  /**
   * Get all GitHub repositories in an installation
   *
   * @param installationId the id of the installation
   * @return list of GitHub repos, or empty list if the installation id doesn't exist
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
    } catch (InstallationCollectionException e) {
      return false;
    }
  }

  /**
   * Adds a new GitHub repository to an existing installtion in the database. If the installation id
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
    } catch (InstallationCollectionException e) {
      return false;
    }
  }

  /**
   * Adds a new method to an existing repo in an existing installation in the database If the
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
    } catch (InstallationCollectionException e) {
      return false;
    }
  }

  /**
   * Gets all methods in a repo
   *
   * @param installationId the id of the installation
   * @param repoId the id of the repo
   * @return a set of methods, or null if the installation or GitHub repository doesn't exist
   */
  public Set<Method> getMethodsFromRepo(String installationId, String repoId) {
    try {
      return service.getMethodsFromRepo(installationId, repoId);
    } catch (InstallationCollectionException e) {
      return null;
    }
  }

  /**
   * Deletes an installation from the database with all its repos
   * 
   * @param installationId the installation to be deleted
   * @return true if successful, otherwise false
   */
  public boolean deleteInstallation(String installationId) {
    try {
      service.deleteInstallationById(installationId);
      return true;
    } catch (InstallationCollectionException e) {
      return false;
    }
  }

  /**
   * Deletes a GitHub repository from the database with all its repos
   * 
   * @param installationId the installation where the Github repository is placed
   * @param repoId the GitHub repository to be deleted
   * @return true if successful, otherwise false
   */
  public boolean deleteGitHubRepo(String installationId, String repoId) {
    try {
      service.deleteGitHubRepo(installationId, repoId);
      return true;
    } catch (InstallationCollectionException e) {
      return false;
    }
  }

}
