package com.icetlab.performancebot.stats;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class BarPlotIssueFormatterTest {
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
  BarPlotIssueFormatter formatter;

  @BeforeEach
  public void resetDatabase() {
    mongoTemplate.dropCollection(Installation.class);
    installationService.addInstallation("an id");
    installationService.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
  }

  @Test
  public void testFormatResultsOneClass() {
    List<Result> old, neew, another;
    old = new ArrayList<>();
    neew = new ArrayList<>();
    another = new ArrayList<>();
    old.add(new Result(Constants.res_oldWay));
    old.add(new Result(Constants.res_oldWay));
    neew.add(new Result(Constants.res_newWay));
    another.add(new Result(Constants.res_newWay_AnotherClassName));

    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.oldWay", old));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", neew));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.AnotherClassName.newWay", another));

    formatter.formatBenchmarkIssue(Constants.EXAMPLE_RESULT);
    assertTrue(true);
  }

}
