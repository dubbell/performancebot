package com.icetlab.performancebot.stats;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import com.icetlab.performancebot.webhook.PayloadManager;

@SpringBootTest
public class BarPlotIssueFormatterTest {

  @InjectMocks
  private PayloadManager payloadHandler;

  @BeforeEach
  public void setUp() {
    payloadHandler = spy(new PayloadManager());
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
  MethodByClassFormatter methodByClassFormatter;


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
    neew.add(new Result(Constants.res_newWay));
    another.add(new Result(Constants.res_newWay_AnotherClassName));

    neew.add(new Result(Constants.EXAMPLE_RESULT_new));
    old.add(new Result(Constants.EXAMPLE_RESULT_old));

    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.oldWay", old));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", neew));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.AnotherClassName.newWay", another));

    //String md = formatter.formatBenchmarkIssue(Constants.EXAMPLE_RESULT);
    String md = methodByClassFormatter.formatBenchmarkIssue(Constants.EXAMPLE_RESULT);
    // Write to file
    try {
      Files.writeString(Path.of("test.md"), md);
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertTrue(true);
  }


}
