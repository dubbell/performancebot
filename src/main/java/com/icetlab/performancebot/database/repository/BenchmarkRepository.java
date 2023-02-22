package com.icetlab.performancebot.database.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import com.icetlab.performancebot.database.model.Benchmark;

@Repository
public interface BenchmarkRepository extends MongoRepository<Benchmark, String> {
  @Query("{ 'id' : ?0 }")
  Optional<Benchmark> findById(String id);
}
