package com.icetlab.performancebot.stats;

import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.icetlab.performancebot.database.model.*;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.icetlab.performancebot.database.controller.InstallationController;
import com.icetlab.performancebot.database.service.InstallationService;
import com.icetlab.performancebot.webhook.PayloadManager;


@SpringBootTest
public class MethodByClassFormatterTest {

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
  public void resetDatabase() throws InstallationCollectionException {
    mongoTemplate.dropCollection(Installation.class);
    installationService.addInstallation("an id");
    installationService.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
  }

  /*
    Test that markdown string contains one class name header, i.e. SampleBenchmarkTest
    and not AnotherClassName
   */
  @Test
  public void testFormatResultsOneClass() throws InstallationCollectionException {
    List<Result> oldWayResults = new ArrayList<>();
    oldWayResults.add(new Result(Constants.exampleResultSampleBenchmarkOldWay));
    oldWayResults.add(new Result(Constants.bmResultSampleBenchmarkOldWay)); // Manually add result from new benchmark

    List<Result> newWayResults = new ArrayList<>();
    newWayResults.add(new Result(Constants.exampleResultSampleBenchmarkNewWay));
    newWayResults.add(new Result(Constants.bmResultSampleBenchmarkNewWay));

    List<Result> anotherClassResults = new ArrayList<>();
    anotherClassResults.add(new Result(Constants.exampleResultAnotherClassNameNewWay));

    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.oldWay", oldWayResults));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", newWayResults));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.AnotherClassName.newWay", anotherClassResults));

    String md = methodByClassFormatter.formatBenchmarkIssue(Constants.bmResultOneClass);

    Assert.assertTrue(md.contains("# SampleBenchmarkTest"));
    Assert.assertFalse(md.contains("# AnotherClassName"));
  }

  /*
    Test that markdown string only contains exactly two class name headers, SampleBenchmarkTest
    and AnotherClassName
   */
  @Test
  public void testFormatResultsMultipleClasses() throws InstallationCollectionException {
    List<Result> oldWayResults = new ArrayList<>();
    oldWayResults.add(new Result(Constants.exampleResultSampleBenchmarkOldWay));
    oldWayResults.add(new Result(
        Constants.bmResultSampleBenchmarkOldWay)); // Manually add result from new benchmark

    List<Result> newWayResults = new ArrayList<>();
    newWayResults.add(new Result(Constants.exampleResultSampleBenchmarkNewWay));
    newWayResults.add(new Result(Constants.bmResultSampleBenchmarkNewWay));

    List<Result> anotherClassResults = new ArrayList<>();
    anotherClassResults.add(new Result(Constants.exampleResultAnotherClassNameNewWay));

    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.oldWay", oldWayResults));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", newWayResults));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.AnotherClassName.newWay", anotherClassResults));

    String md = methodByClassFormatter.formatBenchmarkIssue(Constants.bmResultMultipleClasses);

    Assert.assertTrue(md.contains("# SampleBenchmarkTest"));
    Assert.assertTrue(md.contains("# AnotherClassName"));
  }

  /*
    Test that two classes containing a method with the same name formats correctly
   */
  @Test
  public void testNoDuplicateMethods() throws InstallationCollectionException {
    List<Result> oldWayResults, newWayResults, otherResults;
    oldWayResults = new ArrayList<>();
    newWayResults = new ArrayList<>();
    otherResults = new ArrayList<>();
    oldWayResults.add(new Result(Constants.exampleResultSampleBenchmarkOldWay));
    oldWayResults.add(new Result(Constants.bmResultSampleBenchmarkOldWay));
    newWayResults.add(new Result(Constants.bmResultSampleBenchmarkNewWay)); //duplicate
    otherResults.add(new Result(Constants.bmResultAnotherClassNameNewWay));

    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.oldWay", oldWayResults));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", newWayResults));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.AnotherClassName.newWay", otherResults));

    String firstClassNameHeader = "# SampleBenchmarkTest";
    String lastClassNameHeader = "# AnotherClassName";
    String methodDuplicate = "## newWay";

    String md = methodByClassFormatter.formatBenchmarkIssue(Constants.bmResultMultipleClasses)
        .strip();

    int firstMethodPos = md.indexOf(methodDuplicate);
    int lastMethodPos = md.indexOf(methodDuplicate, firstMethodPos + 1);
    int firstClassPos = md.indexOf(firstClassNameHeader);
    int lastClassPos = md.indexOf(lastClassNameHeader);

    // check that duplicate method is in markdown exactly two times
    Assert.assertTrue(firstMethodPos < lastMethodPos);
    Assert.assertNotEquals(lastMethodPos, -1);
    Assert.assertEquals(md.indexOf(methodDuplicate, lastMethodPos + 1), -1);

    // check that the first one is sub header of first class
    Assert.assertTrue((firstMethodPos > firstClassPos) && (firstMethodPos < lastClassPos));

    // check that the last one is sub header of the second class
    Assert.assertTrue((lastMethodPos > firstClassPos) && (lastMethodPos > lastClassPos));

  }
}
