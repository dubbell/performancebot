package com.icetlab.benchmark_worker.configuration;

import java.util.List;

public class ConfigData
{
    private String language;
    private String buildTool;
    private List<String> include;
    private List<String> exclude;
    private int forks;

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

    public List<String> getInclude()
    {
        return include;
    }

    public void setInclude(List<String> include)
    {
        this.include = include;
    }

    public List<String> getExclude()
    {
        return exclude;
    }

    public void setExclude(List<String> exclude)
    {
        this.exclude = exclude;
    }

    public int getForks()
    {
        return forks;
    }

    public void setForks(int forks)
    {
        this.forks = forks;
    }
}
