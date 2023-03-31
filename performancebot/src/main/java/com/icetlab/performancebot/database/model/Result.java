package com.icetlab.performancebot.database.model;

import java.util.Date;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "results")
public class Result {
  @Field("data")
  String data;
  Date addedAt;

  public Result(String data) {
    this.data = data;
    this.addedAt = new Date();
  }

  public String getData() {
    return this.data;
  }

  public Date getAddedAt() {
    return this.addedAt;
  }
}
