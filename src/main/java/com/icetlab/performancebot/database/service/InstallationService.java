package com.icetlab.performancebot.database.service;

import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.repository.InstallationRepository;
import com.icetlab.performancebot.database.model.GitHubRepo;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * NOT complete. Many things must be added
 */
@Service
public class InstallationService {

  @Autowired
  private InstallationRepository repo;

  public List<GitHubRepo> getReposByInstallationId(String installationId) {
    if (installationExists(installationId))
      return repo.findAllReposById(installationId);

    throw new RuntimeException("No such installation id");
  }


  /**
   * Returns a GitHubRepo object by id, throws a NoSuchElementException if the repo doesn't exist
   * @param installationId the id of the installation
   * @return a GitHubRepo
   */
  public Installation getInstallationById(String installationId) {
    return repo.findById(installationId).orElseThrow();
  }

  /**
   * Adds an installation to the database
   * @param installationId the id of the installation
   * @return the added installation
   */
  public Installation addInstallation(String installationId) {
    if (!installationExists(installationId)) {
      Installation inst = new Installation(installationId, new ArrayList<>());
      repo.insert(inst);
      return inst;
    }

    throw new RuntimeException("The id already exists");
  }

  /**
   * Checks if an installation exists
   * @param installationId the id of the installation
   * @return true if the installation exists, false otherwise
   */
  private boolean installationExists(String installationId) {
    List<Installation> installations = repo.findAll();
    return installations.stream().anyMatch(inst -> inst.getInstallationId().equals(installationId));
  }
}
