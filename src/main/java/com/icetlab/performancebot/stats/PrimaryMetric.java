
package com.icetlab.performancebot.stats;

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
@JsonPropertyOrder({
    "score",
    "scoreError",
    "scoreConfidence",
    "scorePercentiles",
    "scoreUnit",
    "rawData"
})
@Generated("jsonschema2pojo")
public class PrimaryMetric {

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("score")
    private Double score;
    /**
     *
     * (Required)
     *
     */
    @JsonProperty("scoreError")
    private Double scoreError;
    /**
     *
     * (Required)
     *
     */
    @JsonProperty("scoreConfidence")
    private List<Object> scoreConfidence;
    /**
     *
     * (Required)
     *
     */
    @JsonProperty("scorePercentiles")
    private ScorePercentiles scorePercentiles;
    /**
     *
     * (Required)
     *
     */
    @JsonProperty("scoreUnit")
    private String scoreUnit;
    /**
     *
     * (Required)
     *
     */
    @JsonProperty("rawData")
    private List<Object> rawData;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("score")
    public Double getScore() {
        return score;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("score")
    public void setScore(Double score) {
        this.score = score;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("scoreError")
    public Double getScoreError() {
        return scoreError;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("scoreError")
    public void setScoreError(Double scoreError) {
        this.scoreError = scoreError;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("scoreConfidence")
    public List<Object> getScoreConfidence() {
        return scoreConfidence;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("scoreConfidence")
    public void setScoreConfidence(List<Object> scoreConfidence) {
        this.scoreConfidence = scoreConfidence;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("scorePercentiles")
    public ScorePercentiles getScorePercentiles() {
        return scorePercentiles;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("scorePercentiles")
    public void setScorePercentiles(ScorePercentiles scorePercentiles) {
        this.scorePercentiles = scorePercentiles;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("scoreUnit")
    public String getScoreUnit() {
        return scoreUnit;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("scoreUnit")
    public void setScoreUnit(String scoreUnit) {
        this.scoreUnit = scoreUnit;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("rawData")
    public List<Object> getRawData() {
        return rawData;
    }

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("rawData")
    public void setRawData(List<Object> rawData) {
        this.rawData = rawData;
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
