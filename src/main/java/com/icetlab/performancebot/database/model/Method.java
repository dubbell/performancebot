package com.icetlab.performancebot.database.model;

import java.util.List;

public class Method {

    private final String methodName;
    private final List<String> runResults;

    public Method(String methodName, List<String> runResults) {
        this.methodName = methodName;
        this.runResults = runResults;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<String> getRunResults() {
        return runResults;
    }
}
