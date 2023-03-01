package com.icetlab.performancebot.database.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * A model class that represents a collection in the database 
 */
@Document(collection = "installations")
public class Installation {
    @Id
    private final String installationId;
    @Field("repos")
    private List<GitHubRepo> repos;

    public Installation(String installationId, List<GitHubRepo> repos) {
        this.installationId = installationId;
        this.repos = repos;
    }

    public String getInstallationId() {
        return installationId;
    }

    public List<GitHubRepo> getRepos() {
        return repos;
    }
}
