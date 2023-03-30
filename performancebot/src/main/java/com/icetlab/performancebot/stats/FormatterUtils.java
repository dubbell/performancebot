package com.icetlab.performancebot.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.icetlab.performancebot.database.model.Method;

public class FormatterUtils {
  /**
   * Groups methods by class name. The class name is the second to last part of the method name.
   * 
   * @param methods the methods to group
   * @return a map with the class name as key and a list of methods as value
   */
  public static Map<String, List<Method>> groupMethodsByClassName(Set<Method> methods) {
    Map<String, List<Method>> methodMap = new HashMap<>();
    for (Method method : methods) {
      String fullName = method.getMethodName();
      String[] stringParts = fullName.split("\\.");
      String className = stringParts[Math.max(stringParts.length - 2, 0)];
      List<Method> m = methodMap.get(className);
      if (m == null) {
        m = new ArrayList<>();
      }
      m.add(method);
      methodMap.put(className, m);
    }
    return methodMap;
  }
}
