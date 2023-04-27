package com.icetlab.performancebot.webhook;

import com.icetlab.performancebot.webhook.handlers.InstallationHandler;
import com.icetlab.performancebot.webhook.handlers.PullRequestHandler;
import com.icetlab.performancebot.webhook.handlers.RepoHandler;
import com.icetlab.performancebot.webhook.handlers.ResultsHandler;
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
  @Autowired
  private RepoHandler repoHandler;

  /**
   * Handles the payload received from GitHub when the results of a performance test are ready.
   * 
   * @param payload the payload received from BenchmarkWorker
   */
  public boolean handleResults(String payload) {
    boolean handled = resultsHandler.handle(payload);
    return handled;
  }

  /**
   * Handles the payload received from GitHub when a new installation is created.
   *
   * @param payload the payload received from GitHub
   */
  public boolean handleInstall(String payload) {
    boolean handled = installationHandler.handle(payload);
    return handled;
  }

  /**
   * Handles the payload received from GitHub when a pull request event is received. If it does not
   * contain the ping <code>[performancebot]</code> in the message body or title, the request is
   * ignored.
   *
   * @param payload the payload received from GitHub
   */

  public boolean handlePullRequest(String payload) {
    boolean handled = pullRequestHandler.handle(payload);
    return handled;
  }

  public boolean handleRepo(String payload) {
    boolean handled = repoHandler.handle(payload);
    return handled;
  }

}
