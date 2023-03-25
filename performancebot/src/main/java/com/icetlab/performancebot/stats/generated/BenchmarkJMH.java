
package com.icetlab.performancebot.stats.generated;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "jmhVersion", "benchmark", "mode", "threads", "forks", "jvm", "jvmArgs",
        "jdkVersion", "vmName", "vmVersion", "warmupIterations", "warmupTime", "warmupBatchSize",
        "measurementIterations", "measurementTime", "measurementBatchSize", "params",
        "primaryMetric", "secondaryMetrics" })

@Generated("jsonschema2pojo")
public class BenchmarkJMH {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("jmhVersion")
    private String jmhVersion;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("benchmark")
    private String benchmark;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("mode")
    private String mode;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("threads")
    private Integer threads;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("forks")
    private Integer forks;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("jvm")
    private String jvm;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("jvmArgs")
    private List<Object> jvmArgs;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("jdkVersion")
    private String jdkVersion;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vmName")
    private String vmName;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vmVersion")
    private String vmVersion;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("warmupIterations")
    private Integer warmupIterations;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("warmupTime")
    private String warmupTime;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("warmupBatchSize")
    private Integer warmupBatchSize;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("measurementIterations")
    private Integer measurementIterations;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("measurementTime")
    private String measurementTime;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("measurementBatchSize")
    private Integer measurementBatchSize;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("params")
    private Params params;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("primaryMetric")
    private PrimaryMetric primaryMetric;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("secondaryMetrics")
    private SecondaryMetrics secondaryMetrics;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("jmhVersion")
    public String getJmhVersion() {
        return jmhVersion;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("jmhVersion")
    public void setJmhVersion(String jmhVersion) {
        this.jmhVersion = jmhVersion;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("benchmark")
    public String getBenchmark() {
        return benchmark;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("benchmark")
    public void setBenchmark(String benchmark) {
        this.benchmark = benchmark;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("mode")
    public String getMode() {
        return mode;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("mode")
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("threads")
    public Integer getThreads() {
        return threads;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("threads")
    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("forks")
    public Integer getForks() {
        return forks;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("forks")
    public void setForks(Integer forks) {
        this.forks = forks;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("jvm")
    public String getJvm() {
        return jvm;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("jvm")
    public void setJvm(String jvm) {
        this.jvm = jvm;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("jvmArgs")
    public List<Object> getJvmArgs() {
        return jvmArgs;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("jvmArgs")
    public void setJvmArgs(List<Object> jvmArgs) {
        this.jvmArgs = jvmArgs;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("jdkVersion")
    public String getJdkVersion() {
        return jdkVersion;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("jdkVersion")
    public void setJdkVersion(String jdkVersion) {
        this.jdkVersion = jdkVersion;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vmName")
    public String getVmName() {
        return vmName;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vmName")
    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vmVersion")
    public String getVmVersion() {
        return vmVersion;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vmVersion")
    public void setVmVersion(String vmVersion) {
        this.vmVersion = vmVersion;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("warmupIterations")
    public Integer getWarmupIterations() {
        return warmupIterations;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("warmupIterations")
    public void setWarmupIterations(Integer warmupIterations) {
        this.warmupIterations = warmupIterations;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("warmupTime")
    public String getWarmupTime() {
        return warmupTime;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("warmupTime")
    public void setWarmupTime(String warmupTime) {
        this.warmupTime = warmupTime;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("warmupBatchSize")
    public Integer getWarmupBatchSize() {
        return warmupBatchSize;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("warmupBatchSize")
    public void setWarmupBatchSize(Integer warmupBatchSize) {
        this.warmupBatchSize = warmupBatchSize;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("measurementIterations")
    public Integer getMeasurementIterations() {
        return measurementIterations;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("measurementIterations")
    public void setMeasurementIterations(Integer measurementIterations) {
        this.measurementIterations = measurementIterations;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("measurementTime")
    public String getMeasurementTime() {
        return measurementTime;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("measurementTime")
    public void setMeasurementTime(String measurementTime) {
        this.measurementTime = measurementTime;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("measurementBatchSize")
    public Integer getMeasurementBatchSize() {
        return measurementBatchSize;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("measurementBatchSize")
    public void setMeasurementBatchSize(Integer measurementBatchSize) {
        this.measurementBatchSize = measurementBatchSize;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("params")
    public Params getParams() {
        return params;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("params")
    public void setParams(Params params) {
        this.params = params;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("primaryMetric")
    public PrimaryMetric getPrimaryMetric() {
        return primaryMetric;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("primaryMetric")
    public void setPrimaryMetric(PrimaryMetric primaryMetric) {
        this.primaryMetric = primaryMetric;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("secondaryMetrics")
    public SecondaryMetrics getSecondaryMetrics() {
        return secondaryMetrics;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("secondaryMetrics")
    public void setSecondaryMetrics(SecondaryMetrics secondaryMetrics) {
        this.secondaryMetrics = secondaryMetrics;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
