package com.icetlab.performancebot.stats.visualization;

import com.icetlab.performancebot.database.model.Method;

public interface VisualizationStrategy {

  String visualizeBenchmarkResults(Method method);

}
