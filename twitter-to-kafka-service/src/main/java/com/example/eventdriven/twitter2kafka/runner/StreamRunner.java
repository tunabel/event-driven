package com.example.eventdriven.twitter2kafka.runner;

import twitter4j.TwitterException;

public interface StreamRunner {
  void start() throws TwitterException;

}
