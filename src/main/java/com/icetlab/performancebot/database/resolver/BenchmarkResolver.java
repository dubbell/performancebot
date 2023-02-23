package com.icetlab.performancebot.database.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import com.icetlab.performancebot.database.model.Benchmark;
import com.icetlab.performancebot.database.repository.BenchmarkRepository;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@Controller
public class BenchmarkResolver {

  @Autowired
  private BenchmarkRepository benchmarkRepository;

  public Benchmark getBenchmarkById(String id) {
    return benchmarkRepository.findById(id).orElse(null);
  }

  public void addBenchmark(String id, String runData, String projectId) {
    benchmarkRepository.insert(new Benchmark(id, runData, projectId));
  }

  @Bean
  public GraphQLSchema benchmarkSchema() {
    TypeDefinitionRegistry typeRegistry = new TypeDefinitionRegistry();

    String schema = """
            type Benchmark {
              id: ID!
              runData: String!
              projectId: String!
            }
            type Query {
              benchmark(id: ID!): Benchmark
            }
            type Mutation {
              addBenchmark(id: ID!, runData: String!, projectId: String!): Benchmark
            }
        """;

    typeRegistry.merge(new SchemaParser().parse(schema));

    RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
        .type("Benchmark",
            builder -> builder
                .dataFetcher("id", environment -> ((Benchmark) environment.getSource()).getId())
                .dataFetcher("runData",
                    environment -> ((Benchmark) environment.getSource()).getRunData())
                .dataFetcher("projectId",
                    environment -> ((Benchmark) environment.getSource()).getProjectId()))
        .type("Query",
            builder -> builder.dataFetcher("benchmark",
                environment -> getBenchmarkById(environment.getArgument("id"))))
        .type("Mutation", builder -> builder.dataFetcher("addBenchmark", environment -> {
          String id = environment.getArgument("id");
          String runData = environment.getArgument("runData");
          String projectId = environment.getArgument("projectId");
          addBenchmark(id, runData, projectId);
          return getBenchmarkById(id);
        })).build();

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    return schemaGenerator.makeExecutableSchema(typeRegistry, wiring);
  }
}
