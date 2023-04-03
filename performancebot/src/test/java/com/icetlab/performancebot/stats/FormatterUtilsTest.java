package com.icetlab.performancebot.stats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.model.Result;

public class FormatterUtilsTest {
  static String EXAMPLE_RESULT = """
      {
        "issue_url": "an url",
        "repo_id": "a repo id",
        "name": "a repo name",
        "installation_id": "an id",
        "results": [
          {
            "benchmark": "functionname",
          }
        ]
      }
          """;

  @Test
  public void testGroupMethodsByClassName() {
    List<Result> results = new ArrayList<>();
    results.add(new Result(EXAMPLE_RESULT));
    Method method = new Method("org.dinmamma.Class1.aFunction", results);
    Method method2 = new Method("org.dinmamma.Class2.aFunction", results);
    Set<Method> methods = new HashSet<>();
    methods.add(method);
    methods.add(method2);
    Map<String, List<Method>> methodMap = FormatterUtils.groupMethodsByClassName(methods);
    assertEquals(methodMap.size(), 2);
    assertEquals(methodMap.get("Class1").size(), 1);
    assertEquals(methodMap.get("Class2").size(), 1);
    assertEquals(methodMap.get("Class3"), null);
  }

  @Test
  public void testGroupMethodsByClassNameWithSameClassName() {
    List<Result> results = new ArrayList<>();
    results.add(new Result(EXAMPLE_RESULT));
    Method method = new Method("org.dinmamma.Class1.aFunction", results);
    Method method2 = new Method("org.dinmamma.Class1.aFunction2", results);
    Set<Method> methods = new HashSet<>();
    methods.add(method);
    methods.add(method2);
    Map<String, List<Method>> methodMap = FormatterUtils.groupMethodsByClassName(methods);
    assertEquals(methodMap.size(), 1);
    assertEquals(methodMap.get("Class1").size(), 2);
  }
}
