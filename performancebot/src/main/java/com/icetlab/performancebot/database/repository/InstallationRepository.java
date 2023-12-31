package com.icetlab.performancebot.database.repository;

import com.icetlab.performancebot.database.model.GitHubRepo;
import com.icetlab.performancebot.database.model.Installation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface InstallationRepository extends MongoRepository<Installation, String> {

  /**
   * Finds all installations (GitHubRepo) by id
   * 
   * @param installationId the id of the installations to find
   * @return a list of installations (GitHubReps objects)
   */
  @Query(value = "{ 'installationId' : ?0 }", fields = "{'repos' : 1 }")
  List<GitHubRepo> findAllReposById(String installationId);

  /**
   * Deletes a GitHubRepo from an installation
   * @param installationId the installation where the GitHubRepo belongs to
   * @param repoId the id of the GitHubRepo to be deleted
   */
  @Query("{'installationId' : ?0, 'repos.repoId' : ?1}")
  void deleteGitHubRepoByInstallationIdAndRepoId(String installationId, String repoId);

}
