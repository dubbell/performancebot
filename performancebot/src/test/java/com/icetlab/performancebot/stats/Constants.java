package com.icetlab.performancebot.stats;

class Constants {

  // Example of a run result from benchmark worker
  static String bmResultOneClass = """
      {
          "issue_url": "an url",
          "repo_id": "a repo id",
          "name": "a repo name",
          "installation_id": "an id",
          "PRNumber": "1",
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
                      },
                      "commit" : "acommit"
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
                          "score" : 10.3596125,
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
                      },
                      "commit:" : "acommit"
                  }
              ]

          ]
        }
      """;

  static String bmResultMultipleClasses = """
      {
          "issue_url": "an url",
          "repo_id": "a repo id",
          "name": "a repo name",
          "installation_id": "an id",
          "PRNumber": "1",
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
                      },
                      "commit:" : "acommit"
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
                          "score" : 10.3596125,
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
                      },
                      "commit:" : "acommit"
                  },
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
                          "score" : 10.3596125,
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
                      },
                      "commit:" : "acommit"
                  }
              ]

          ]
        }
          """;

  static String bmResultSampleBenchmarkOldWay = """
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
                          "score" : 10.3596125,
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
                      },
                      "commit:" : "acommit"
                  }
      """;

  static String bmResultSampleBenchmarkNewWay = """
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
                      },
                      "commit:" : "acommit"
                  }
      """;

  static String bmResultAnotherClassNameNewWay = """
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
                          "score" : 10.3596125,
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
                      },
                      "commit:" : "acommit"
                  },
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
                          "score" : 10.3596125,
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
                      },
                      "commit:" : "acommit"
                  }
      """;


  // Examples of run results
  static String exampleResultSampleBenchmarkOldWay = """
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
          },
          "commit:" : "acommit"
      }
          """;

  static String exampleResultSampleBenchmarkNewWay = """
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
          },
          "commit:" : "acommit"
      }
          """;

  static String exampleResultAnotherClassNameNewWay = """
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
          },
          "commit:" : "acommit"
      }
          """;

  static String exampleResultSampleBenchmarknewWay2 = """
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
              "score" : 17.3596125,
              "scoreError" : "NaN",
              "scoreConfidence" : [
                  "NaN",
                  "NaN"
              ],
              "scorePercentiles" : {
                  "0.0" : 14.2345123,
                  "50.0" : 18.3596125,
                  "90.0" : 22.92065025,
                  "95.0" : 22.92065025,
                  "99.0" : 22.92065025,
                  "99.9" : 22.92065025,
                  "99.99" : 22.92065025,
                  "99.999" : 22.92065025,
                  "99.9999" : 22.92065025,
                  "100.0" : 24.1234554
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
          },
          "commit:" : "acommit"
      }
          """;

  static String exampleResultNewWayRunConfigs = """
      {
          "benchmark" : "com.szatmary.peter.SampleBenchmarkTest.newWay",
          "mode" : "avgt",
          "threads" : 4,
          "forks" : 1,
          "warmupIterations" : 2,
          "warmupTime" : "1 s",
          "warmupBatchSize" : 1,
          "measurementIterations" : 4,
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
          },
          "commit:" : "acommit"
      }
          """;

}
