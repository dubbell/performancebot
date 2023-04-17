package com.icetlab.performancebot.stats;

class Constants {

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

  static String res_oldWay = """
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

  static String res_newWay = """
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

  static String res_newWay_AnotherClassName = """
      {
          "benchmark" : "com.szatmary.peter.AnotherClassName.newWay",
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
              "score" : 20.3596125,
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

}
