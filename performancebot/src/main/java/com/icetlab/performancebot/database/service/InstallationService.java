package com.icetlab.performancebot.database.service;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.model.Result;
import com.icetlab.performancebot.database.repository.InstallationRepository;
import com.icetlab.performancebot.database.model.GitHubRepo;
import java.util.NoSuchElementException;
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


  public List<GitHubRepo> getReposByInstallationId(String installationId) {
    if (installationExists(installationId)) {
      return repo.findAllReposById(installationId);
    }

    throw new RuntimeException("No such installation id");
  }


  /**
   * Returns a GitHubRepo object by id, throws a NoSuchElementException if the repo doesn't exist
   *
   * @param installationId the id of the installation
   * @return a GitHubRepo
   */
  public Installation getInstallationById(String installationId) {
    return repo.findById(installationId).orElseThrow();
  }

  /**
   * Adds an installation to the database
   *
   * @param installationId the id of the installation
   * @throws IllegalArgumentException if the installation already exists
   */
  public void addInstallation(String installationId) {
    if (!installationExists(installationId)) {
      Installation inst = new Installation(installationId, new ArrayList<>());
      repo.insert(inst);
      return;
    }

    throw new IllegalArgumentException("The id already exists");
  }

  /**
   * Adds a GitHub repo (GitHubRepo object) to an installation in the database
   *
   * @param installationId the id of the installation where GitHub repo will be added
   * @param repo the repo to be added
   */
  public void addRepoToInstallation(String installationId, GitHubRepo repo) {
    if (installationExists(installationId)) {
      if (repoExists(installationId, repo.getRepoId())) {
        throw new IllegalArgumentException("Repo already exists");
      }

      mongoTemplate.updateFirst(Query.query(where("_id").is(installationId)),
          new Update().push("repos", repo), Installation.class);
      return;
    }

    throw new NoSuchElementException("No such installation id");
  }

  private boolean repoExists(String installationId, String repoId) {
    Installation inst = getInstallationById(installationId);
    return inst.getRepos().stream().anyMatch(r -> r.getRepoId().equals(repoId));
  }

  /**
   * Adds a method to a repo in an installation
   *
   * @param installationId the id of the installation
   * @param repoId the id of the repo
   * @param method the method to be added
   */
  public void addMethodToRepo(String installationId, String repoId, Method method) {
    Installation inst = getInstallationById(installationId);
    // Check if method exists
    if (inst.getRepos().stream().anyMatch(r -> r.getRepoId().equals(repoId))) {
      Optional<GitHubRepo> gitHubRepo =
          inst.getRepos().stream().filter(r -> r.getRepoId().equals(repoId)).findFirst();
      if (gitHubRepo.isPresent()) {
        if (gitHubRepo.get().getMethods().stream()
            .anyMatch(m -> m.getMethodName().equals(method.getMethodName()))) {
          throw new IllegalArgumentException("Method already exists");
        }
      }
    }

    if (inst.getRepos().stream().anyMatch(r -> r.getRepoId().equals(repoId))) {
      mongoTemplate.updateFirst(
          Query.query(where("_id").is(installationId).and("repos.repoId").is(repoId)),
          new Update().push("repos.$.methods", method), Installation.class);
      return;
    }

    throw new NoSuchElementException("No such repo id");
  }

  /**
   * Adds a run result to a method.
   *
   * @param installationId the id of the installation
   * @param repoId the id of the repo
   * @param methodName the name of the method
   * @param result the result to be added
   */
  public void addRunResultToMethod(String installationId, String repoId, String methodName,
      String result) {
    Installation inst = mongoTemplate.findById(installationId, Installation.class);
    if (inst == null) {
      throw new NoSuchElementException("No such installation id");
    }

    Optional<GitHubRepo> gitHubRepo =
        inst.getRepos().stream().filter(r -> r.getRepoId().equals(repoId)).findFirst();
    if (gitHubRepo.isPresent()) {
      Method method =
          gitHubRepo.get().getMethods().stream().filter(m -> m.getMethodName().equals(methodName))
              .findFirst().orElseThrow(NoSuchElementException::new);
      method.addResult(new Result(result));
      mongoTemplate.save(inst);
      return;
    }
    throw new NoSuchElementException("No such repo");
  }

  /**
   * Retrieves all (benchmark) methods from a repo
   *
   * @param installationId the id of the installation where repo exists
   * @param repoId the id of the repo
   * @return set of methods (Method objects)
   */
  public Set<Method> getMethodsFromRepo(String installationId, String repoId) {
    Installation inst = getInstallationById(installationId);
    Optional<GitHubRepo> gitHubRepo =
        inst.getRepos().stream().filter(r -> r.getRepoId().equals(repoId)).findFirst();
    if (gitHubRepo.isPresent()) {
      return gitHubRepo.get().getMethods();
    }

    throw new NoSuchElementException("No such repo id");
  }
  
  /**
   * Deletes an installation and all its related data from the database.
   * Throws NoSuchElementException if the installation does not exist in the database.
   * @param installationId the id of the installation to be deleted
   */
  public void deleteInstallationById(String installationId) {
    if (!installationExists(installationId)) {
      throw new NoSuchElementException("No such installation id");
    }
    // remove it otherwise
    repo.deleteById(installationId);
  }
  
  /**
   * Deletes a Github repository from the database
   * Throws NoSuchElementException if the installation or the Github repository do not exist in the database
   * @param installationId the id of the installation where the Github repository is stored
   * @param repoId the id of the Github repository to be deleted
   */
  public void deleteGitHubRepo(String installationId, String repoId) {
    Installation installation = repo.findById(installationId)
            .orElseThrow(() -> new NoSuchElementException("No such installation"));

    List<GitHubRepo> repos = installation.getRepos();
    // Delete the repo
    final boolean isDeleted = repos.removeIf(repo -> repo.getRepoId().equals(repoId));

    if (isDeleted) {
      // Update the installation
      repo.save(installation);
    } else {
      throw new NoSuchElementException("No such GitHub repo");
    }
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
