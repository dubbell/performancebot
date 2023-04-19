package com.icetlab.performancebot.github.webhook;

class WebhookMocks {
  final static String INSTALL_EVENT = """
      {
          "action": "created",
          "installation": {
              "id": 123456
          },
          "repositories": [
              {
                  "id": 1234123,
                  "node_id": "asdf",
                  "name": "reponame",
                  "full_name": "acc/reponame",
                  "private": false
              }
          ]
        }
            """;

  final static String UNINSTALL_EVEMT = """
      {
          "action": "removed",
          "installation": {
              "id": 123456
          },
          "repositories": [
              {
                  "id": 1234123,
                  "node_id": "asdf",
                  "name": "reponame",
                  "full_name": "acc/reponame",
                  "private": false
              }
          ]
        }
      """;

  final static String PR_WITHOUT_PING = """
          {
            "action": "reopened",
            "number": 3,
            "pull_request": {
                "url": "https://api.github.com/repos/samkaj/bouncing_balls/pulls/3",
                "id": 1319613051,
                "html_url": "https://github.com/samkaj/bouncing_balls/pull/3",
                "diff_url": "https://github.com/samkaj/bouncing_balls/pull/3.diff",
                "patch_url": "https://github.com/samkaj/bouncing_balls/pull/3.patch",
                "issue_url": "https://api.github.com/repos/samkaj/bouncing_balls/issues/3",
                "state": "open",
                "locked": false,
                "title": "Samuels implementation",
                "user": {
                    "login": "samkaj",
                    "id": 23452345,
                },
                "body": null,
                "head": {
                    "label": "samkaj:samuel",
                    "ref": "samuel",
                    "sha": "3fe70638a129bd47c71d0211f2e82a44ba71e50e",
                    "user": {
                        "login": "samkaj",
                        "id": 23452345,
                    },
                    "repo": {
                        "id": 23452345,
                        "node_id": "R_kgDOIBhzOw",
                        "name": "bouncing_balls",
                        "full_name": "samkaj/bouncing_balls",
                        "private": true,
                    }
                },
            },
            "repository": {
                "id": 23452345,
                "node_id": "R_kgDOIBhzOw",
                "name": "bouncing_balls",
                "full_name": "samkaj/bouncing_balls",
                "private": true,
                "clone_url": "example.com"
            },
            "installation": {
                "id": 36609505,
                "node_id": "MDIzOkludGVncmF0aW9uSW5zdGFsbGF0aW9uMzY2MDk1MDU="
            }
          }
      """;

  final static String PR_WITH_PING = """
          {
            "action": "reopened",
            "number": 3,
            "pull_request": {
                "url": "https://api.github.com/repos/samkaj/bouncing_balls/pulls/3",
                "id": 1319613051,
                "html_url": "https://github.com/samkaj/bouncing_balls/pull/3",
                "diff_url": "https://github.com/samkaj/bouncing_balls/pull/3.diff",
                "patch_url": "https://github.com/samkaj/bouncing_balls/pull/3.patch",
                "issue_url": "https://api.github.com/repos/samkaj/bouncing_balls/issues/3",
                "state": "open",
                "locked": false,
                "title": "Samuels implementation [performancebot]",
                "user": {
                    "login": "samkaj",
                    "id": 23452345
                },
                "body": null,
                "head": {
                    "label": "samkaj:samuel",
                    "ref": "samuel",
                    "sha": "3fe70638a129bd47c71d0211f2e82a44ba71e50e",
                    "user": {
                        "login": "samkaj",
                        "id": 23452345
                    },
                    "repo": {
                        "id": 23452345,
                        "node_id": "R_kgDOIBhzOw",
                        "name": "bouncing_balls",
                        "full_name": "samkaj/bouncing_balls",
                        "private": true
                    }
                }
            },
            "repository": {
                "id": 23452345,
                "node_id": "R_kgDOIBhzOw",
                "name": "bouncing_balls",
                "full_name": "samkaj/bouncing_balls",
                "private": true,
                "clone_url": "example.com"
            },
            "installation": {
                "id": 36609505,
                "node_id": "MDIzOkludGVncmF0aW9uSW5zdGFsbGF0aW9uMzY2MDk1MDU="
            }
          }
      """;

  final static String RESULT_EVENT = """
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
}
