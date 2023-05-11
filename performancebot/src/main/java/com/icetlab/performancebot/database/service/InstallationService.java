package com.icetlab.performancebot.database.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.icetlab.performancebot.database.model.*;
import com.icetlab.performancebot.database.repository.InstallationRepository;

import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InstallationService {

  @Autowired
  private InstallationRepository repo;
  @Autowired
  private MongoTemplate mongoTemplate;

  /**
   * Gets all GitHub repositories that exist in an installation
   * @param installationId id of the installation
   * @return List of GitHub repositories
   */
  public List<GitHubRepo> getReposByInstallationId(String installationId) {
    if (!installationExists(installationId)) {
      throw new RuntimeException("No such installation id");
    }

    return repo.findAllReposById(installationId);
  }

  /**
   * Returns an Installation object by id, throws a NoSuchElementException if the installation doesn't exist
   *
   * @param installationId the id of the installation
   * @return a Installation
   */
  public Installation getInstallationById(String installationId) {
    return repo.findById(installationId).orElseThrow();
  }

  /**
   * Adds an installation to the database
   *
   * @param installationId the id of the installation
   * @throws InstallationCollectionException if the installation already exists
   */
  public void addInstallation(String installationId) throws InstallationCollectionException {
    if (installationExists(installationId)) {
      throw InstallationCollectionException.raiseException(InstallationCollectionException.INSTALLATION_EXISTS);
    }

    Installation inst = new Installation(installationId, new ArrayList<>());
    repo.insert(inst);
  }

  /**
   * Adds a GitHub repo (GitHubRepo object) to an installation in the database
   *
   * @param installationId the id of the installation where GitHub repo will be added
   * @param repo the repo to be added
   */
  public void addRepoToInstallation(String installationId, GitHubRepo repo) throws InstallationCollectionException {
    if (!installationExists(installationId)) {
      throw InstallationCollectionException.raiseException(InstallationCollectionException.NO_SUCH_INSTALLATION);
    }

    else {
      if (repoExists(installationId, repo.getRepoId())) {
        throw InstallationCollectionException.raiseException(InstallationCollectionException.REPO_EXISTS);
      }

      mongoTemplate.updateFirst(Query.query(where("_id").is(installationId)),
              new Update().push("repos", repo), Installation.class);
    }
  }

  /**
   * Adds a method to a repo in an installation
   *
   * @param installationId the id of the installation
   * @param repoId the id of the repo
   * @param method the method to be added
   */
  public void addMethodToRepo(String installationId, String repoId, Method method) throws InstallationCollectionException {
    Installation installation = getInstallationById(installationId);

    Optional<GitHubRepo> gitHubRepo = installation.getRepos().stream()
            .filter(r -> r.getRepoId().equals(repoId))
            .findFirst();

    if (gitHubRepo.isPresent()) {
      if (gitHubRepo.get().getMethods().stream()
              .anyMatch(m -> m.getMethodName().equals(method.getMethodName()))) {
        throw InstallationCollectionException.raiseException(InstallationCollectionException.METHOD_EXISTS);
      }

      // Add the method to the GitHub repository
      mongoTemplate.updateFirst(
              Query.query(where("_id").is(installationId).and("repos.repoId").is(repoId)),
              new Update().push("repos.$.methods", method), Installation.class);

    } else {
      throw InstallationCollectionException.raiseException(InstallationCollectionException.NO_SUCH_REPO);
    }
  }

  /**
   * Adds a run result to a method.
   *
   * @param installationId the id of the installation
   * @param repoId the id of the repo
   * @param methodName the name of the method
   * @param result the result to be added
   */
  public void addRunResultToMethod(String installationId, String repoId, String methodName, String result) throws InstallationCollectionException {
    Installation inst = mongoTemplate.findById(installationId, Installation.class);

    if (inst == null) {
      throw InstallationCollectionException.raiseException(InstallationCollectionException.NO_SUCH_INSTALLATION);
    }

    GitHubRepo gitHubRepo = inst.getRepos().stream()
            .filter(r -> r.getRepoId().equals(repoId))
            .findFirst()
            .orElseThrow(() -> InstallationCollectionException.raiseException(InstallationCollectionException.NO_SUCH_REPO));

    Method method = gitHubRepo.getMethods().stream()
            .filter(m -> m.getMethodName().equals(methodName))
            .findFirst()
            .orElseThrow(() -> InstallationCollectionException.raiseException(InstallationCollectionException.NO_SUCH_METHOD));

    method.addResult(new Result(result));
    mongoTemplate.save(inst);
  }

  /**
   * Retrieves all (benchmark) methods from a repo
   *
   * @param installationId the id of the installation where repo exists
   * @param repoId the id of the repo
   * @return set of methods (Method objects)
   */
  public Set<Method> getMethodsFromRepo(String installationId, String repoId) throws InstallationCollectionException {
    Installation inst = getInstallationById(installationId);
    Optional<GitHubRepo> gitHubRepo = inst.getRepos().stream().filter(r -> r.getRepoId().equals(repoId)).findFirst();
    if (gitHubRepo.isEmpty()) {
      throw InstallationCollectionException.raiseException(InstallationCollectionException.NO_SUCH_REPO);
    }

    return gitHubRepo.get().getMethods();
  }
  
  /**
   * Deletes an installation and all its related data from the database.
   * Throws NoSuchElementException if the installation does not exist in the database.
   * @param installationId the id of the installation to be deleted
   */
  public void deleteInstallationById(String installationId) throws InstallationCollectionException {
    if (!installationExists(installationId)) {
      throw InstallationCollectionException.raiseException(InstallationCollectionException.NO_SUCH_INSTALLATION);
    }
    // remove it otherwise
    repo.deleteById(installationId);
  }
  
  /**
   * Deletes a GitHub repository from the database
   * Throws NoSuchElementException if the installation or the Github repository do not exist in the database
   * @param installationId the id of the installation where the Github repository is stored
   * @param repoId the id of the GitHub repository to be deleted
   */
  public void deleteGitHubRepo(String installationId, String repoId) throws InstallationCollectionException {
    Installation installation = repo.findById(installationId)
            .orElseThrow(() -> InstallationCollectionException.raiseException(InstallationCollectionException.NO_SUCH_INSTALLATION));

    List<GitHubRepo> repos = installation.getRepos();
    // Delete the repo
    final boolean isDeleted = repos.removeIf(repo -> repo.getRepoId().equals(repoId));

    if (isDeleted) {
      // Update the installation
      repo.save(installation);
    } else {
      throw InstallationCollectionException.raiseException(InstallationCollectionException.NO_SUCH_REPO);
    }
  }

  /**
   * Checks if a GitHub repository exists
   *
   * @param installationId the id of the installation
   * @param repoId the id of the GitHub repository
   * @return true if the GitHub repository exists, false otherwise
   */
  private boolean repoExists(String installationId, String repoId) {
    Installation inst = getInstallationById(installationId);
    return inst.getRepos().stream().anyMatch(r -> r.getRepoId().equals(repoId));
  }

  /**
   * Checks if an installation exists
   *
   * @param installationId the id of the installation
   * @return true if the installation exists, false otherwise
   */
  private boolean installationExists(String installationId) {
    List<Installation> installations = repo.findAll();
    return installations.stream().anyMatch(inst -> inst.getInstallationId().equals(installationId));
  }
}
