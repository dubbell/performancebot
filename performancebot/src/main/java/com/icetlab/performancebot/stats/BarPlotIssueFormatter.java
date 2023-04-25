package com.icetlab.performancebot.stats;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icetlab.performancebot.database.model.Method;
import com.icetlab.performancebot.database.model.Result;
import com.icetlab.performancebot.database.service.InstallationService;

@Component
public class BarPlotIssueFormatter implements BenchmarkIssueFormatter {

    @Autowired
    InstallationService installationService;

    /**
     * Takes a jmh results and creates a markdown string containing bar plot
     * that compares the results to historical benchmark data
     *
     * @param jmhResults json formatted jmh results
     * @return a markdown formatted issue of the jmh results compared to
     * historical data in bar plots
     */
    @Override
    public String formatBenchmarkIssue(String jmhResults) {
        JsonNode node;
        try {
            node = new ObjectMapper().readTree(jmhResults);
            String installationId = node.get("installation_id").asText();
            String repoId = node.get("repo_id").asText();
            List<String> methodNames
                    = FormatterUtils.getMethodNamesFromCurrentRun(jmhResults);
            Set<Method> methods = FormatterUtils.filterMethodsFromCurrentRun(
                    installationService.getMethodsFromRepo(installationId, repoId),
                    methodNames);
            Map<String, List<Method>> classes
                    = FormatterUtils.groupMethodsByClassName(methods);
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
     * # ClassName
     * for method in methods:
     * ## MethodName
     * ![methodName](imageUrl)
     * other run information
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
     * Takes a method and returns a markdown string containing method header,
     * run results plot and configuration data
     *
     * @param method the method to be formatted
     * @return a formatted md string of the method
     */
    private String formatMethodBody(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(
                String.format("## %s\n", FormatterUtils.getMethodNameFromBenchmarkField(
                        method.getMethodName())));
        String url;
        try {
            CategoryChart methodBarPlot = buildBarplot(method);
            String encodedBarPlot = encodeChartToBase64(methodBarPlot);
            url = uploadImage(writeChartToPng(methodBarPlot, method.getMethodName()),
                    encodedBarPlot);
        } catch (Exception e) {
            url = "https://ih1.redbubble.net/image.1539738010.3563/flat,750x,075,f-pad,750x1000,f8f8f8.u1.jpg";
        }
        sb.append(String.format("![%s](%s)\n", method.getMethodName(), url));
        sb.append("\nTODO: implement additional info\n");
        return sb.toString();
    }

    /**
     * @param method the method which results should be visualized as bar plot
     * png
     * @return path of the bar plot png
     * @throws IOException if png image cannot be created
     */
    private CategoryChart buildBarplot(Method method) throws IOException {
        // FIXME: Loops through all results, it should be the 10 latest
        CategoryChart barPlot = new CategoryChartBuilder()
                .width(600)
                .height(600)
                .title(method.getMethodName())
                .xAxisTitle("Date")
                .yAxisTitle("Score")
                .build();
        List<Result> results = method.getRunResults();
        List<String> timestamps
                = results.stream()
                        .map((x)
                                -> new SimpleDateFormat("dd/MM/yyyy HH:MM")
                                .format(x.getAddedAt()))
                        .toList();
        List<Double> scores
                = results.stream()
                        .map((x)
                                -> Double.parseDouble(
                                FormatterUtils.getScoreFromPrimaryMetric(x.getData())))
                        .toList();
        String header
                = FormatterUtils.getMethodNameFromBenchmarkField(method.getMethodName());
        barPlot.addSeries(header, timestamps, scores);
        barPlot.getStyler().setLegendVisible(false);
        return barPlot;
    }

    /**
     * Takes a Category chart and encodes it as a base64 string
     *
     * @param categoryChart the chart to be encoded
     * @return a base64 string
     * @throws IOException
     */
    private String encodeChartToBase64(CategoryChart categoryChart)
            throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BitmapEncoder.saveBitmap(categoryChart, outputStream,
                BitmapEncoder.BitmapFormat.PNG);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    private String writeChartToPng(CategoryChart chart, String fileName)
            throws IOException {
        BitmapEncoder.saveBitmapWithDPI(chart, fileName, BitmapFormat.PNG, 300);
        return fileName + ".png";
    }

    /**
     * Uploads an image to imagekit and returns the imageUrl if successful.
     *
     * @throws IOException if prviate key cannot be loaded
     */
    private String uploadImage(String fileName, String base64Img) throws IOException {
        HttpHeaders headers = generateHeaders();
        MultiValueMap<String, Object> body = generateRequestBodyFromFile(fileName, base64Img);
        ResponseEntity<String> response = postToImageKit(body, headers);
        String imageUrl = getUrlFromResponse(response.getBody());
        return imageUrl;
    }

    /**
     * Generates headers for sending POST request to imagekit upload file
     * endpoint
     *
     * @return HttpHeaders
     * @throws IOException if private key cannot be loaded from properties file
     */
    private HttpHeaders generateHeaders() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        String encodedPrivateKey = Base64.getEncoder().encodeToString(readKeyFromProps().getBytes());
        headers.setBasicAuth(encodedPrivateKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    /**
     * @param base64Img the image (encoded as base64 string) to be uploaded to
     * imagekit
     * @param fileName the name of the file when uploaded to imagekit
     */
    private MultiValueMap<String, Object> generateRequestBodyFromFile(String fileName, String base64Img) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", base64Img);
        body.add("fileName", fileName);
        return body;
    }

    private ResponseEntity<String> postToImageKit(MultiValueMap<String, Object> body, HttpHeaders headers) {
        String endpoint = "https://upload.imagekit.io/api/v1/files/upload/";
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, requestEntity, String.class);
        return response;
    }

    private String getUrlFromResponse(String body)
            throws JsonMappingException, JsonProcessingException {
        JsonNode node = new ObjectMapper().readTree(body);
        String url = node.get("url").asText();
        return url;
    }

    private String readKeyFromProps() throws IOException {
        Properties prop = new Properties();
        FileInputStream properties
                = new FileInputStream("src/main/resources/application.properties");
        prop.load(properties);
        String privateKey = prop.getProperty("imagekit.privatekey");
        return privateKey;
    }
}
