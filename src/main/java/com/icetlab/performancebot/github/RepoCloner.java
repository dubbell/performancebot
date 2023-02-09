package com.icetlab.performancebot.github;

/**
 * Responsible for cloning the target repository that should be tested.
 */
public class RepoCloner {

  private final Auth auth;

  public RepoCloner() {
    auth = new Auth();
    auth.authorize();
  }


  public void cloneRepo(String url) {
    try {
      auth.authorize();
      System.out.println("Clone repo!");
    } catch (Exception e) {
      System.out.println("Bad stuff");
    }
  }
}