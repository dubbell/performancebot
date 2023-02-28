package com.icetlab.performancebot.database.repository;

import com.icetlab.performancebot.database.model.GitHubRepo;
import com.icetlab.performancebot.database.model.Installation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface InstallationRepository extends MongoRepository<Installation, String> {

    List<String> findAllIds();
    
    /**
     * Finds all installations (GitHubRepo) by id
     * @param installationId the id of the installations to find
     * @return a list of installations (GitHubReps objects)
     */
    @Query(value = "{ 'installationId' : ?0 }", fields = "{'repos' : 1 }")
    List<GitHubRepo> findAllInstallationsById(String installationId);
}
