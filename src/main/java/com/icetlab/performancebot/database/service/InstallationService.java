package com.icetlab.performancebot.database.service;

import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.repository.InstallationRepository;
import com.icetlab.performancebot.database.model.GitHubRepo;
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
        if (repo.findAllIds().contains(installationId))
            return repo.findAllInstallationsById(installationId);

        throw new RuntimeException("No such installation id");
    }

    public Installation addInstallation(String installationId) {
        if (!repo.findAllIds().contains(installationId)) {
            Installation inst = new Installation(installationId, new ArrayList<>());
            repo.save(inst);

        }

        throw new RuntimeException("The id is already exists");
    }

    public GitHubRepo addGitHubRepo(String installationId, String repoId) {

    }
}
