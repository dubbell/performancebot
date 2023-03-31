package com.icetlab.performancebot.stats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.icetlab.performancebot.database.controller.InstallationController;
import com.icetlab.performancebot.database.model.GitHubRepo;
import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.model.Result;
import com.icetlab.performancebot.database.service.InstallationService;
import com.icetlab.performancebot.github.Payload;

@SpringBootTest
public class GitHubIssueFormatterTest {
  static String EXAMPLE_RESULT = """
      {
          "issue_url": "an url",
          "repo_id": "a repo id",
          "name": "a repo name",
          "installation_id": "an id",
          "results": [
              [
                  {
                      "benchmark" : "com.szatmary.peter.SampleBenchmarkTest.newWay",
                      "mode" : "avgt",
                      "threads" : 4,
                      "forks" : 1,
                      "warmupIterations" : 2,
                      "warmupTime" : "1 s",
                      "warmupBatchSize" : 1,
                      "measurementIterations" : 2,
                      "measurementTime" : "1 ms",
                      "measurementBatchSize" : 1,
                      "primaryMetric" : {
                          "score" : 27.06890025,
                          "scoreError" : "NaN",
                          "scoreConfidence" : [
                              "NaN",
                              "NaN"
                          ],
                          "scorePercentiles" : {
                              "0.0" : 20.2825505,
                              "50.0" : 27.06890025,
                              "90.0" : 33.85525,
                              "95.0" : 33.85525,
                              "99.0" : 33.85525,
                              "99.9" : 33.85525,
                              "99.99" : 33.85525,
                              "99.999" : 33.85525,
                              "99.9999" : 33.85525,
                              "100.0" : 33.85525
                          },
                          "scoreUnit" : "ms/op",
                          "rawData" : [
                              [
                                  20.2825505,
                                  33.85525
                              ]
                          ]
                      },
                      "secondaryMetrics" : {
                      }
                  },
                  {
                      "benchmark" : "com.szatmary.peter.SampleBenchmarkTest.oldWay",
                      "mode" : "avgt",
                      "threads" : 4,
                      "forks" : 1,
                      "warmupIterations" : 2,
                      "warmupTime" : "1 s",
                      "warmupBatchSize" : 1,
                      "measurementIterations" : 2,
                      "measurementTime" : "1 ms",
                      "measurementBatchSize" : 1,
                      "primaryMetric" : {
                          "score" : 18.3596125,
                          "scoreError" : "NaN",
                          "scoreConfidence" : [
                              "NaN",
                              "NaN"
                          ],
                          "scorePercentiles" : {
                              "0.0" : 13.79857475,
                              "50.0" : 18.3596125,
                              "90.0" : 22.92065025,
                              "95.0" : 22.92065025,
                              "99.0" : 22.92065025,
                              "99.9" : 22.92065025,
                              "99.99" : 22.92065025,
                              "99.999" : 22.92065025,
                              "99.9999" : 22.92065025,
                              "100.0" : 22.92065025
                          },
                          "scoreUnit" : "ms/op",
                          "rawData" : [
                              [
                                  13.79857475,
                                  22.92065025
                              ]
                          ]
                      },
                      "secondaryMetrics" : {
                      }
                  }
              ]

          ]
        }
          """;

  static String res1 = """
      {
          "benchmark" : "com.szatmary.peter.SampleBenchmarkTest.oldWay",
          "mode" : "avgt",
          "threads" : 4,
          "forks" : 1,
          "warmupIterations" : 2,
          "warmupTime" : "1 s",
          "warmupBatchSize" : 1,
          "measurementIterations" : 2,
          "measurementTime" : "1 ms",
          "measurementBatchSize" : 1,
          "primaryMetric" : {
              "score" : 18.3596125,
              "scoreError" : "NaN",
              "scoreConfidence" : [
                  "NaN",
                  "NaN"
              ],
              "scorePercentiles" : {
                  "0.0" : 13.79857475,
                  "50.0" : 18.3596125,
                  "90.0" : 22.92065025,
                  "95.0" : 22.92065025,
                  "99.0" : 22.92065025,
                  "99.9" : 22.92065025,
                  "99.99" : 22.92065025,
                  "99.999" : 22.92065025,
                  "99.9999" : 22.92065025,
                  "100.0" : 22.92065025
              },
              "scoreUnit" : "ms/op",
              "rawData" : [
                  [
                      13.79857475,
                      22.92065025
                  ]
              ]
          },
          "secondaryMetrics" : {
          }
      }
          """;

  static String res2 = """
      {
          "benchmark" : "com.szatmary.peter.SampleBenchmarkTest.newWay",
          "mode" : "avgt",
          "threads" : 4,
          "forks" : 1,
          "warmupIterations" : 2,
          "warmupTime" : "1 s",
          "warmupBatchSize" : 1,
          "measurementIterations" : 2,
          "measurementTime" : "1 ms",
          "measurementBatchSize" : 1,
          "primaryMetric" : {
              "score" : 18.3596125,
              "scoreError" : "NaN",
              "scoreConfidence" : [
                  "NaN",
                  "NaN"
              ],
              "scorePercentiles" : {
                  "0.0" : 13.79857475,
                  "50.0" : 18.3596125,
                  "90.0" : 22.92065025,
                  "95.0" : 22.92065025,
                  "99.0" : 22.92065025,
                  "99.9" : 22.92065025,
                  "99.99" : 22.92065025,
                  "99.999" : 22.92065025,
                  "99.9999" : 22.92065025,
                  "100.0" : 22.92065025
              },
              "scoreUnit" : "ms/op",
              "rawData" : [
                  [
                      13.79857475,
                      22.92065025
                  ]
              ]
          },
          "secondaryMetrics" : {
          }
      }
          """;

  @InjectMocks
  private Payload payloadHandler;

  @BeforeEach
  public void setUp() {
    payloadHandler = spy(new Payload());
  }

  @InjectMocks
  @Autowired
  InstallationService installationService;

  @InjectMocks
  @Autowired
  InstallationController installationController;

  @Autowired
  MongoTemplate mongoTemplate;

  @Autowired
  GitHubIssueFormatter formatter;

  @BeforeEach
  public void resetDatabase() {
    mongoTemplate.dropCollection(Installation.class);
    installationService.addInstallation("an id");
    installationService.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
  }


  @Test
  public void testFormatResultsOneClass() {
    // Populate database
    List<Result> results = new ArrayList<>();
    results.add(new Result(res1));
    results.add(new Result(res2));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.oldWay", results));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", results));

    String expected = """
        # SampleBenchmarkTest

        ## oldWay

        | Timestamp |  Min  | Max | Score | Unit |
        |-----------|-------|-----|-------|------|
        | 31/03/2023 09:03 |  22.92 | 13.80 |  18.36 | ms/op |
        | 31/03/2023 09:03 |  22.92 | 13.80 |  18.36 | ms/op |

        ## newWay

        | Timestamp |  Min  | Max | Score | Unit |
        |-----------|-------|-----|-------|------|
        | 31/03/2023 09:03 |  22.92 | 13.80 |  18.36 | ms/op |
        | 31/03/2023 09:03 |  22.92 | 13.80 |  18.36 | ms/op |
              """;

    String md = formatter.formatBenchmarkIssue(EXAMPLE_RESULT).strip();
    // Write to file
    try {
      Files.writeString(Path.of("test.md"), md);
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertEquals(expected.strip(), md);
  }

  @Test
  public void testFormatResultsMultipleClasses() {
    // Populate database
    List<Result> results = new ArrayList<>();
    results.add(new Result(res1));
    results.add(new Result(res2));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.oldWay", results));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", results));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.AnotherName.newWay", results));

    String expected = """
        # SampleBenchmarkTest

        ## oldWay

        | Timestamp |  Min  | Max | Score | Unit |
        |-----------|-------|-----|-------|------|
        | 31/03/2023 09:03 |  22.92 | 13.80 |  18.36 | ms/op |
        | 31/03/2023 09:03 |  22.92 | 13.80 |  18.36 | ms/op |

        ## newWay

        | Timestamp |  Min  | Max | Score | Unit |
        |-----------|-------|-----|-------|------|
        | 31/03/2023 09:03 |  22.92 | 13.80 |  18.36 | ms/op |
        | 31/03/2023 09:03 |  22.92 | 13.80 |  18.36 | ms/op |


        # AnotherName

        ## newWay

        | Timestamp |  Min  | Max | Score | Unit |
        |-----------|-------|-----|-------|------|
        | 31/03/2023 09:03 |  22.92 | 13.80 |  18.36 | ms/op |
        | 31/03/2023 09:03 |  22.92 | 13.80 |  18.36 | ms/op |
              """;

    String md = formatter.formatBenchmarkIssue(EXAMPLE_RESULT).strip();
    assertTrue(md.contains(expected.strip()));
  }


}
