package com.icetlab.performancebot.database.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * A model class that represents a collection in the database 
 */
@Document(collation = "installations")
public class Installation {
    @Id
    private final String installationId;
    @Field("installations")
    private List<GitHubRepo> repos;

    public Installation(String installationId, List<GitHubRepo> installations) {
        this.installationId = installationId;
        this.repos = installations;
    }

    public String getInstallationId() {
        return installationId;
    }

    public List<GitHubRepo> getRepos() {
        return repos;
    }
}
