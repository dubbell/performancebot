package com.icetlab.performancebot.stats;

import com.icetlab.performancebot.database.model.Method;

public interface VisualizationStrategy {

  String visualizeBenchmarkResults(Method method);

}
