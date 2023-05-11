package com.icetlab.performancebot.database;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;
import com.icetlab.performancebot.database.model.GitHubRepo;
import com.icetlab.performancebot.database.model.Installation;
import com.icetlab.performancebot.database.model.InstallationCollectionException;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.service.InstallationService;
import com.icetlab.performancebot.webhook.PayloadManager;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import org.springframework.data.mongodb.core.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@SpringBootTest
public class InstallationServiceTest {
  static String EXAMPLE_RESULT = """
      {
        "issue_url": "an url",
        "repo_id": "a repo id",
        "name": "a repo name",
        "installation_id": "an id",
        "results": [
          {
            "jmhVersion": "1.36",
            "benchmark": "functionname",
            "primaryMetric": {
              "score": 100135.8952,
              "scoreError": 36.51692329266034,
              "scoreConfidence": [
                100099.37827670734,
                100172.41212329266
              ],
              "scorePercentiles": {
                "0.0": 100124.313,
              },
              "scoreUnit": "us/op",
              "rawData": [
                [
                  100135.314
                ],
              ]
            },
            "secondaryMetrics": {}
          }
        ]
      }
          """;

  @InjectMocks
  private PayloadManager payloadHandler;

  @BeforeEach
  public void setUp() {
    payloadHandler = spy(new PayloadManager());
  }

  @InjectMocks
  @Autowired
  InstallationService installationService;

  @Autowired
  MongoTemplate mongoTemplate;

  @Test
  public void testAddInstallation() throws InstallationCollectionException {
    installationService.addInstallation("an id");
    Query q = Query.query(where("_id").is("an id"));
    assertEquals(mongoTemplate.find(q, Installation.class).size(), 1);
  }

  @Test
  public void testAddInstallationException() throws InstallationCollectionException {
    installationService.addInstallation("an id");
    assertThrows(InstallationCollectionException.class, () -> {
      installationService.addInstallation("an id");
    });
  }

  @Test
  public void testGetInstallationById() throws InstallationCollectionException {
    installationService.addInstallation("an id");
    assertEquals(installationService.getInstallationById("an id").getInstallationId(), "an id");
  }

  @Test
  public void testGetInstallationByIdException() {
    assertThrows(NoSuchElementException.class, () -> {
      installationService.getInstallationById("an id");
    });
  }

  @Test
  public void testAddRepoToInstallation() throws InstallationCollectionException {
    installationService.addInstallation("an id");
    installationService.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
    Query q = Query.query(where("_id").is("an id"));
    assertEquals(mongoTemplate.find(q, Installation.class).get(0).getRepos().size(), 1);
  }

  @Test
  public void testAddRepoThatAlreadyExists() throws InstallationCollectionException {
    installationService.addInstallation("an id");
    installationService.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
    assertThrows(InstallationCollectionException.class, () -> {
      installationService.addRepoToInstallation("an id",
          new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
    });
  }

  @Test
  public void testAddRepoToInstallationWhenTheInstallationDoesNotExist() {
    assertThrows(InstallationCollectionException.class, () -> {
      installationService.addRepoToInstallation("an id",
          new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
    });
  }

  @Test
  public void testAddMethodToRepo() throws InstallationCollectionException {
    installationService.addInstallation("an id");
    installationService.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("a method", new ArrayList<>()));
    Query q = Query.query(where("_id").is("an id"));
    assertEquals(
        mongoTemplate.find(q, Installation.class).get(0).getRepos().get(0).getMethods().size(), 1);
  }

  @Test
  public void testAddMultipleMethodsToRepo() throws InstallationCollectionException {
    installationService.addInstallation("an id");
    installationService.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("a method", new ArrayList<>()));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("another method", new ArrayList<>()));
    Query q = Query.query(where("_id").is("an id"));
    assertEquals(
        mongoTemplate.find(q, Installation.class).get(0).getRepos().get(0).getMethods().size(), 2);
  }

  @Test
  public void testAddMultipleMethodsWithTheSameName() throws InstallationCollectionException {
    installationService.addInstallation("an id");
    installationService.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("a method", new ArrayList<>()));
    assertThrows(InstallationCollectionException.class, () -> {
      installationService.addMethodToRepo("an id", "a repo id",
          new Method("a method", new ArrayList<>()));
    });
  }

  @Test
  public void testAddMultipleMethodsWithDifferentNames() throws InstallationCollectionException {
    installationService.addInstallation("an id");
    installationService.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("a method", new ArrayList<>()));
    installationService.addMethodToRepo("an id", "a repo id",
        new Method("another method", new ArrayList<>()));
    Query q = Query.query(where("_id").is("an id"));
    assertEquals(
        mongoTemplate.find(q, Installation.class).get(0).getRepos().get(0).getMethods().size(), 2);
  }

  @Test
  public void testAddRunResultToMethod() throws InstallationCollectionException {
    Method method = new Method("functionname", new ArrayList<>());
    installationService.addInstallation("an id");
    installationService.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
    installationService.addMethodToRepo("an id", "a repo id", method);
    installationService.addRunResultToMethod("an id", "a repo id", "functionname", EXAMPLE_RESULT);
    installationService.getMethodsFromRepo("an id", "a repo id").contains(method);
  }

  @Test
  public void testAddRunResultToMethodThatDoesNotExist() throws InstallationCollectionException {
    installationService.addInstallation("an id");
    installationService.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
    assertThrows(InstallationCollectionException.class, () -> {
      installationService.addRunResultToMethod("an id", "a repo id", "functionname",
          EXAMPLE_RESULT);
    });
  }

  @Test
  public void testAddMultipleResultsToMethod() throws InstallationCollectionException {
    Method method = new Method("functionname", new ArrayList<>());
    installationService.addInstallation("an id");
    installationService.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
    installationService.addMethodToRepo("an id", "a repo id", method);
    installationService.addRunResultToMethod("an id", "a repo id", "functionname", EXAMPLE_RESULT);
    installationService.addRunResultToMethod("an id", "a repo id", "functionname", EXAMPLE_RESULT);
    Set<Method> methods = installationService.getMethodsFromRepo("an id", "a repo id");
    assertEquals(methods.size(), 1);
    assertEquals(methods.iterator().next().getRunResults().size(), 2);
  }

  @Test
  public void testAddMultipleResultsToDifferentMethods() throws InstallationCollectionException {
    Method method = new Method("functionname", new ArrayList<>());
    Method method2 = new Method("functionname2", new ArrayList<>());
    installationService.addInstallation("an id");
    installationService.addRepoToInstallation("an id",
        new GitHubRepo("a repo id", new HashSet<>(), "a repo name"));
    installationService.addMethodToRepo("an id", "a repo id", method);
    installationService.addMethodToRepo("an id", "a repo id", method2);
    installationService.addRunResultToMethod("an id", "a repo id", "functionname", EXAMPLE_RESULT);
    installationService.addRunResultToMethod("an id", "a repo id", "functionname2", EXAMPLE_RESULT);
    Set<Method> methods = installationService.getMethodsFromRepo("an id", "a repo id");
    assertEquals(methods.size(), 2);
    assertEquals(methods.iterator().next().getRunResults().size(), 1);
  }

  @Test
  public void testDeleteInstallationById() throws InstallationCollectionException {
    // Add an installation
    String installationId = "an id";
    installationService.addInstallation(installationId);

    // Delete it using the deleteInstallationById()
    installationService.deleteInstallationById(installationId);

    Query q = Query.query(where("_id").is(installationId));
    assertEquals(mongoTemplate.find(q, Installation.class).size(), 0);
  }

  @Test
  public void testDeleteInstallationByIdMultipleInstallations() throws InstallationCollectionException {
    // Add multiple installations
    String id1 = "id 1";
    String id2 = "id 2";
    String id3 = "id 3";
    installationService.addInstallation(id1);
    installationService.addInstallation(id2);
    installationService.addInstallation(id3);

    // Add all the ids to be able to include them in the query
    List<String> ids = Arrays.asList(id1, id2, id3);

    // Delete all installations using the deleteInstallationById()
    installationService.deleteInstallationById(id1);
    installationService.deleteInstallationById(id2);
    installationService.deleteInstallationById(id3);

    Query q = Query.query(where("_id").in(ids));
    assertEquals(mongoTemplate.find(q, Installation.class).size(), 0);
  }

  @Test
  public void testDeleteInstallationByIdNoSuchElementException() {
    // Some invalid id
    String installationId = "invalid id";

    assertThrows(InstallationCollectionException.class, () -> {
      installationService.deleteInstallationById(installationId);
    });
  }

  @Test
  public void testDeleteInstallationByIdThrowsExceptionWhenGivenMultipleIdsWithOneInvalid() throws InstallationCollectionException {
    String validId = "valid id";
    String invalidId = "invalid id";
    installationService.addInstallation(validId);

    // Delete both IDs at the same time
    assertThrows(InstallationCollectionException.class, () -> {
      installationService.deleteInstallationById(validId);
      installationService.deleteInstallationById(invalidId);
    });
  }
  
  @Test
  public void testDeleteGitHubRepo() throws InstallationCollectionException {
    String installation = "id for installation";
    GitHubRepo repository = new GitHubRepo("id for reoo", new HashSet<Method>(), "name");

    installationService.addInstallation(installation);
    installationService.addRepoToInstallation(installation, repository);

    // delete the repo
    installationService.deleteGitHubRepo(installation, repository.getRepoId());
    
    Query q = Query.query(where("_id").is(installation));
    List<Installation> installations = mongoTemplate.find(q, Installation.class);
    
    assertEquals(0, installations.get(0).getRepos().size());
  }
  
  @Test
  public void testDeleteGitHubRepoNotFoundInstallation(){
    String nonExistingInstallationId = "non existing id";
    GitHubRepo nonExistingRepo = new GitHubRepo("id", new HashSet<>(), "repo1");

    final InstallationCollectionException e = assertThrows(InstallationCollectionException.class, () -> {
      installationService.deleteGitHubRepo(nonExistingInstallationId, nonExistingRepo.getRepoId());
    });

    assertEquals(e.getMessage(), InstallationCollectionException.NO_SUCH_INSTALLATION);
  }

  @Test
  public void testDeleteGitHubRepoNotFoundRepo() throws InstallationCollectionException {
    String installationId = "installation id";
    GitHubRepo nonExistingRepo = new GitHubRepo("id", new HashSet<>(), "repo1");

    installationService.addInstallation(installationId);

    final InstallationCollectionException e = assertThrows(InstallationCollectionException.class, () -> {
      installationService.deleteGitHubRepo(installationId, nonExistingRepo.getRepoId());
    });

    assertEquals(e.getMessage(), InstallationCollectionException.NO_SUCH_REPO);
  }

  @BeforeEach
  public void tearDown() {
    mongoTemplate.dropCollection(Installation.class);
  }

}
