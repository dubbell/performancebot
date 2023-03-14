package com.icetlab.benchmark_worker.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;

public class ConfigurationFactory
{
    /**
     * Gets project configuration from .yaml file.
     */
    public static Configuration getConfiguration() throws Exception {

        // read config data from repository
        ConfigData configData = new ObjectMapper(new YAMLFactory()).readValue(new File("benchmark_directory/perfbot.yaml"), ConfigData.class);

        if (configData.getLanguage().equalsIgnoreCase("java")) {
            if (configData.getBuildTool().equalsIgnoreCase("maven")) {
                return new MavenConfiguration(configData);
            }
            else {
                throw new Exception("Invalid project configuration for the performance bot: invalid build tool.");
            }
        }
        else {
            throw new Exception("Invalid project configuration for the performance bot: invalid language.");
        }
    }
}
