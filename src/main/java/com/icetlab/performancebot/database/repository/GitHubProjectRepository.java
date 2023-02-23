package com.icetlab.performancebot.database.repository;

import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.icetlab.performancebot.database.model.Benchmark;
import com.icetlab.performancebot.database.model.GitHubProject;

public interface GitHubProjectRepository extends MongoRepository<GitHubProject, String> {
  @Query("{ 'name' : ?0, 'owner' : ?1 }")
  GitHubProject findByNameAndOwner(String name, String owner);

  @NotNull
  @Query("{ 'id' : ?0}")
  Optional<GitHubProject> findById(@NotNull String id);

  @Query("{ 'url' : ?0}")
  GitHubProject findByUrl(String url);

  @Query("{ 'id' : ?0, 'runs' : { $exists : true } }")
  List<Benchmark> findAllRunsById(String id);

  @Override
  long count();
}
