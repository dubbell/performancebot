package com.icetlab.performancebot.stats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import com.icetlab.performancebot.github.PayloadManager;

@SpringBootTest
public class TableIssueFormatterTest {

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
  TableIssueFormatter formatter;

  @BeforeEach
  public void resetDatabase() {
    mongoTemplate.dropCollection(Installation.class);
    installationService.addInstallation("an id");
    installationService.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
  }

  /**
   * Test that a jmh result of one class containing two methods only prints out the class name once
   */
  @Test
  public void testFormatResultsOneClass() {
    // Populate database
    List<Result> results = new ArrayList<>();
    results.add(new Result(Constants.res_newWay));
    results.add(new Result(Constants.res_oldWay));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.oldWay", results));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("com.szatmary.peter.SampleBenchmarkTest.newWay", results));

    String classNameHeader = "# SampleBenchmarkTest";

    String md = formatter.formatBenchmarkIssue(Constants.EXAMPLE_RESULT).strip();
    System.out.println("Actual result:");
    System.out.println(md);

    // Write to file
    // try {
    // Files.writeString(Path.of("test.md"), md);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }

    Pattern pattern = Pattern.compile(classNameHeader);
    Matcher matcher = pattern.matcher(md);
    assertTrue((matcher.find() && !matcher.find()));
  }

  /**
   * Test that two classes containing a method with the same name formats correctly
   */
  @Test
  public void testFormatResultsMultipleClasses() {
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

    String firstClassNameHeader = "# SampleBenchmarkTest";
    String lastClassNameHeader = "# AnotherClassName";
    String methodDuplicate = "## newWay";

    String md = formatter.formatBenchmarkIssue(Constants.EXAMPLE_RESULT).strip();
    System.out.println("Actual result");

    // Write to file
    try {
      Files.writeString(Path.of("test.md"), md);
    } catch (IOException e) {
      e.printStackTrace();
    }

    int firstMethodPos = md.indexOf(methodDuplicate);
    int lastMethodPos = md.indexOf(methodDuplicate, firstMethodPos + 1);
    int firstClassPos = md.indexOf(firstClassNameHeader);
    int lastClassPos = md.indexOf(lastClassNameHeader);

    // check that duplicate method is in markdown exactly two times
    assertTrue(firstMethodPos < lastMethodPos);
    assertNotEquals(lastMethodPos, -1);
    assertEquals(md.indexOf(methodDuplicate, lastMethodPos + 1), -1);

    // check that the first one is sub header of first class
    assertTrue((firstMethodPos > firstClassPos) && (firstMethodPos < lastClassPos));

    // check that the last one is sub header of the second class
    assertTrue((lastMethodPos > firstClassPos) && (lastMethodPos > lastClassPos));

  }

}
