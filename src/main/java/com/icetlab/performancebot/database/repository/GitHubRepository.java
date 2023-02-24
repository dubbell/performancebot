package com.icetlab.performancebot.database.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import com.icetlab.performancebot.database.model.Benchmark;
import com.icetlab.performancebot.database.model.GitHub;

public interface GitHubRepository extends MongoRepository<GitHub, String> {
  @Query("{ 'name' : ?0, 'owner' : ?1 }")
  GitHub findByNameAndOwner(String name, String owner);

  @Query("{ 'id' : ?0}")
  Optional<GitHub> findById(String id);

  @Query("{ 'url' : ?0}")
  GitHub findByUrl(String url);

  @Query("{ 'id' : ?0, 'runs' : { $exists : true } }")
  List<Benchmark> findAllRunsById(String id);

  @Override
  long count();
}
