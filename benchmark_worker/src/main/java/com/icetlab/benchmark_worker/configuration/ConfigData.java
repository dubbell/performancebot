package com.icetlab.benchmark_worker.configuration;

import java.util.List;

public class ConfigData
{
    private String language;
    private String buildTool;
    private List<String> regex;

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getBuildTool()
    {
        return buildTool;
    }

    public void setBuildTool(String buildTool)
    {
        this.buildTool = buildTool;
    }

    public List<String> getRegex()
    {
        return regex;
    }

    public void setRegex(List<String> regex)
    {
        this.regex = regex;
    }
}
