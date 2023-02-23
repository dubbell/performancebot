package com.icetlab.performancebot.stats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;

class IssueLogger {

  /**
   * Takes a json string and creates a simple issue. Returns the issue as a string.
   *
   * @param json a json string representing a jmh benchmark
   * @return a simple issue of the benchmark data
   * @throws JsonProcessingException if json string cannot be parsed into a BenchmarkJMH object
   */
  public static String createIssue(String json) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    BenchmarkJMH benchmarkJMH = objectMapper.readValue(json, BenchmarkJMH.class);
    IssueFormatter issueFormatter = new SimpleIssue();
    String issue = issueFormatter.formatBenchmarkIssue(benchmarkJMH);
    return issue;
  }

  /**
   * Reads the jmh-result.json file and creates a simple issue. Returns the issue as a string.
   * <p>
   * Needs to be in root of directory for paths to work.
   *
   * @return the issue of the benchmark in the json file
   * @throws IOException if it cannot find the json file
   */
  public static String createIssue() throws IOException {
    // Get path to json file with benchmark data
    // TODO: Fix a better solution for finding path
    String userdir = System.getProperty("user.dir");
    String path = userdir + "/src/main/java/com/icetlab/performancebot/stats/jmh-result.json";

    // Create ObjectMapper to parse json file to a BenchmarkJMH
    ObjectMapper objectMapper = new ObjectMapper();
    BenchmarkJMH benchmarkJMH = objectMapper.readValue(
        new File(path),
        BenchmarkJMH.class);

    // Create an issue of benchmark
    IssueFormatter issueFormatter = new SimpleIssue();
    String issue = issueFormatter.formatBenchmarkIssue(benchmarkJMH);
    return issue;

  }

  /**
   * Store issue in a text file in logs directory. Filename is identified by time the file is
   * created.
   * <p>
   * Needs to be in root of directory for paths to work.
   *
   * @param issue the issue to be stored in the logs file
   * @throws IOException
   */
  public static void storeIssue(String issue) throws IOException {
    // Use time as identifier of filename
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm");
    LocalDateTime now = LocalDateTime.now();
    String filename = "issue_" + dtf.format(now); // <- filename of issue
    System.out.println(filename);

    // TODO: Make path relative
    String userdir = System.getProperty("user.dir");
    String path = userdir + "/src/main/java/com/icetlab/performancebot/stats/";
    String issuePath = path + "logs/"
        + filename
        + ".txt";
    touch(issuePath);
    writeToFile(issuePath, issue);

  }

  private static void touch(String path) throws IOException {
    File file = new File(path);
    file.createNewFile(); //Create new file.
  }

  private static void writeToFile(String filePath, String content) {
    try {
      FileWriter myWriter = new FileWriter(filePath);
      myWriter.write(content);
      myWriter.close();
      System.out.println("Successfully wrote to the file.");
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }
}

