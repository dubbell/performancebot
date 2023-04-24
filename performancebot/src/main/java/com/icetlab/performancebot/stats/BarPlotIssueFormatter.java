package com.icetlab.performancebot.stats;

import io.imagekit.sdk.ImageKit;
import io.imagekit.sdk.config.Configuration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.model.Result;
import com.icetlab.performancebot.database.service.InstallationService;

@Component
public class BarPlotIssueFormatter implements BenchmarkIssueFormatter {

  @Autowired
  InstallationService installationService;
  String imagesFolderPath;

  /**
   * Takes a jmh results and creates a markdown string containing bar plot that compares the results
   * to historical benchmark data
   *
   * @param jmhResults json formatted jmh results
   * @return a markdown formatted issue of the jmh results compared to historical data in bar plots
   */
  @Override
  public String formatBenchmarkIssue(String jmhResults) {
    JsonNode node;
    try {
      node = new ObjectMapper().readTree(jmhResults);
      String installationId = node.get("installation_id").asText();
      String repoId = node.get("repo_id").asText();
      List<String> methodNames = FormatterUtils.getMethodNamesFromCurrentRun(jmhResults);
      Set<Method> methods = FormatterUtils.filterMethodsFromCurrentRun(
          installationService.getMethodsFromRepo(installationId, repoId), methodNames);
      Map<String, List<Method>> classes = FormatterUtils.groupMethodsByClassName(methods);
      imagesFolderPath = repoId.replaceAll("\\s+", "");
      File imagesFolder = new File(imagesFolderPath);
      imagesFolder.mkdir();
      return formatIssueBody(classes);
    } catch (JsonProcessingException e) {
      return e.toString();
    }
  }

  /**
   * <p>
   * Builds an issue based on the structure of the classes map
   * </p>
   *
   * <pre>
   * <code>
   * for class in classes:
   *   # ClassName
   *   for method in methods:
   *     ## MethodName
   *     ![methodName](url)
   *     other run information
   * </code>
   * </pre>
   *
   * @param classes the classes and their methods to be formatted
   * @return the classes
   */
  private String formatIssueBody(Map<String, List<Method>> classes) {
    StringBuilder sb = new StringBuilder();
    for (String className : classes.keySet()) {
      sb.append(String.format("# %s\n", className));
      for (Method method : classes.get(className)) {
        sb.append(formatMethodBody(method));
      }
    }
    return sb.toString();
  }

  /**
   * Takes a method and returns a markdown string containing method header, run results plot and
   * configuration data
   *
   * @param method the method to be formatted
   * @return a formatted md string of the method
   */
  private String formatMethodBody(Method method) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("## %s\n",
        FormatterUtils.getMethodNameFromBenchmarkField(method.getMethodName())));
    String path;
    try {
      // TODO: Change return type of createBarPlotPng so we don't have to save pngs locally?
      // File pngFile = createBarPlotPng(method)
      // path = uploadImageOnImageKit(pngFile)
      path = createBarPlotPng(method);
    } catch (IOException e) {
      path =
          "https://ih1.redbubble.net/image.1539738010.3563/flat,750x,075,f-pad,750x1000,f8f8f8.u1.jpg";
    }
    sb.append(String.format("![%s](%s)\n", method.getMethodName(), path));
    sb.append("TODO: implement additional info\n");
    return sb.toString();
  }

  /**
   * @param path
   * @return url
   */
  private String uploadImageAndGetPath(String path) {
    // TODO: Should take the created image, upload it somewhere, and get a public URL back to be added to md
    return path;
  }

  /**
   * @param method the method which results should be visualized as bar plot png
   * @return path of the bar plot png
   * @throws IOException if png image cannot be created
   */
  private String createBarPlotPng(Method method) throws IOException {
    CategoryChart barPlot = new CategoryChartBuilder().width(600).height(600)
        .title(method.getMethodName()).xAxisTitle("Date").yAxisTitle("Score").build();
    List<Result> results = method.getRunResults();
    List<String> timestamps = results.stream()
        .map((x) -> new SimpleDateFormat("dd/MM/yyyy HH:MM").format(x.getAddedAt())).toList();
    List<Double> scores = results.stream()
        .map((x) -> Double.parseDouble(FormatterUtils.getScoreFromPrimaryMetric(x.getData())))
        .toList();
    String header = FormatterUtils.getMethodNameFromBenchmarkField(method.getMethodName());
    barPlot.addSeries(header, timestamps, scores);
    barPlot.getStyler().setLegendVisible(false);
    return writeChartToPng(barPlot, method.getMethodName());
  }

  /*
  private String createImageFromResults(List<Result> results, String header) throws IOException {
    CategoryChart chart = new CategoryChartBuilder().width(600).height(600).title(header)
        .xAxisTitle("Date").yAxisTitle("Score").build();
    List<String> timestamps = results.stream()
        .map((x) -> new SimpleDateFormat("dd/MM/yyyy HH:MM").format(x.getAddedAt())).toList();
    List<Double> score = results.stream()
        .map((x) -> Double.parseDouble(FormatterUtils.getScoreFromPrimaryMetric(x.getData())))
        .toList();
    chart.addSeries(header, timestamps, score);
    return writeChartToPng(chart, header);
  }*/

  /**
   * Converts the bar chart to a PNG image, stores it in repo folder and returns the path.
   *
   * @param chart    the chart to be converted to an PNG image
   * @param fileName the name of the PNG file
   * @return the path of the PNG image of the bar chart
   * @throws IOException if png file cannot be created
   */
  private String writeChartToPng(CategoryChart chart, String fileName) throws IOException {
    String pngPath = imagesFolderPath + "/" + fileName;
    BitmapEncoder.saveBitmapWithDPI(chart, pngPath, BitmapFormat.PNG,
        300);
    return pngPath + ".png";
  }

  // TODO: Should move this method somewhere else
  private void authenticateImageKit() throws IOException {
    Properties prop = new Properties();
    FileInputStream properties = new FileInputStream(
        "src\\main\\resources\\application.properties");
    prop.load(properties);
    String publicKey = prop.getProperty("imagekit.publickey");
    String privateKey = prop.getProperty("imagekit.privatekey");
    String URL = prop.getProperty("imagekit.urlendpoint");
    ImageKit imageKit = ImageKit.getInstance();
    Configuration config = new Configuration(publicKey, privateKey, URL);
    imageKit.setConfig(config);
  }


}
