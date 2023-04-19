package com.icetlab.performancebot.github;

import com.icetlab.performancebot.github.webhook.InstallationHandler;
import com.icetlab.performancebot.github.webhook.PullRequestHandler;
import com.icetlab.performancebot.github.webhook.ResultsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PayloadManager {
  @Autowired
  private ResultsHandler resultsHandler;
  @Autowired
  private InstallationHandler installationHandler;
  @Autowired
  private PullRequestHandler pullRequestHandler;

  /**
   * Handles the payload received from GitHub when the results of a performance test are ready.
   * 
   * @param payload the payload received from BenchmarkWorker
   */
  public void handleResults(String payload) {
    resultsHandler.handle(payload);
  }

  /**
   * Handles the payload received from GitHub when a new installation is created.
   *
   * @param payload the payload received from GitHub
   */
  public void handleInstall(String payload) {
    installationHandler.handle(payload);
  }

  /**
   * Handles the payload received from GitHub when a pull request event is received. If it does not
   * contain the ping <code>[performancebot]</code> in the message body or title, the request is
   * ignored.
   *
   * @param payload the payload received from GitHub
   */

  public void handlePullRequest(String payload) {
    pullRequestHandler.handle(payload);
  }

}
