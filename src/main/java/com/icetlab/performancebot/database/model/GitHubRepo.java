package com.icetlab.performancebot.database.model;

import java.util.Set;

public class GitHubRepo {

    private final String repoId;
    private Set<Method> methods;

    public GitHubRepo(String repoId, Set<Method> methods) {
        this.repoId = repoId;
        this.methods = methods;
    }

    public String getRepoId() {
        return repoId;
    }

    public Set<Method> getMethods() {
        return methods;
    }
}
