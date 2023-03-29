package com.icetlab.performancebot.database.model;

import java.util.Date;

public class Result {
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
