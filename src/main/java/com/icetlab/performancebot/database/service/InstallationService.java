package com.icetlab.performancebot.database.service;

import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.repository.InstallationRepository;
import com.icetlab.performancebot.database.model.GitHubRepo;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
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
    if (installationExists(installationId))
      return repo.findAllReposById(installationId);

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
      mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(installationId)),
          new Update().push("repos", repo), Installation.class);
      return;
    }

    throw new RuntimeException("No such installation id");
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
    if (inst.getRepos().stream().anyMatch(r -> r.getRepoId().equals(repoId))) {
      mongoTemplate.updateFirst(
          Query.query(Criteria.where("_id").is(installationId).and("repos.repoId").is(repoId)),
          new Update().push("repos.$.methods", method), Installation.class);
      return;
    }

    throw new NoSuchElementException("No such repo id");
  }

  /**
   * Adds a run result to a method
   * 
   * @param installationId the id of the installation
   * @param repoId the id of the repo
   * @param methodName the name of the method
   * @param result the result to be added
   */
  public void addRunResultToMethod(String installationId, String repoId, String methodName,
      String result) {
    Installation inst = getInstallationById(installationId);
    if (inst.getRepos().stream().anyMatch(r -> r.getRepoId().equals(repoId))) {
      mongoTemplate.updateFirst(
          Query.query(Criteria.where("_id").is(installationId).and("repos.repoId").is(repoId)
              .and("repos.methods.methodName").is(methodName)),
          new Update().push("repos.$.methods.$[].runResults", result), Installation.class);
      return;
    }

    throw new NoSuchElementException("No such method name");
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
