package com.icetlab.performancebot.database.resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import com.icetlab.performancebot.database.model.Benchmark;
import com.icetlab.performancebot.database.model.GitHubProject;
import com.icetlab.performancebot.database.repository.GitHubProjectRepository;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@Component
public class GitHubProjectResolver {
  String schema = """
      type Query {
        projects: [GitHubProject]
      }

      type GitHubProject {
        id: ID!
        name: String!
        owner: String!
        url: String!
        runs: [Benchmark]
      }

      type Benchmark {
        id: ID!
        name: String!
      }
      """;


  @Autowired
  private GitHubProjectRepository gitHubProjectRepository;

  public List<GitHubProject> getProjects() {
    return gitHubProjectRepository.findAll();
  }

  public GitHubProject getProjectById(String id) {
    Optional<GitHubProject> project = gitHubProjectRepository.findById(id);
    return project.orElse(null);
  }

  public List<Benchmark> getRunsById(String id) {
    return gitHubProjectRepository.findAllRunsById(id);
  }

  public void addProject(String id, String name, String owner, String url) {
    Optional<GitHubProject> project = gitHubProjectRepository.findById(id);
    if (project.isPresent()) {
      return;
    }
    gitHubProjectRepository.insert(new GitHubProject(id, name, owner, url, new ArrayList<>()));
  }

  @Bean
  public GraphQLSchema githubProjectSchema() {
    TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schema);
    RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
        .type("Query", builder -> builder.dataFetcher("projects", environment -> getProjects()))
        .type("GitHubProject", builder -> builder
            .dataFetcher("id", environment -> ((GitHubProject) environment.getSource()).getId())
            .dataFetcher("name", environment -> ((GitHubProject) environment.getSource()).getName())
            .dataFetcher("owner",
                environment -> ((GitHubProject) environment.getSource()).getOwner())
            .dataFetcher("url", environment -> ((GitHubProject) environment.getSource()).getUrl())
            .dataFetcher("runs",
                environment -> ((GitHubProject) environment.getSource()).getRuns()))
        .build();
    return new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);
  }
}
