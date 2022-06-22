package com.example.eventdriven.twitter2kafka.runner.impl;

import com.example.eventdriven.config.TwitterToKafkaServiceConfigData;
import com.example.eventdriven.twitter2kafka.listener.TwitterKafkaStatusListener;
import com.example.eventdriven.twitter2kafka.runner.StreamRunner;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@Component
@RequiredArgsConstructor
@Log4j2
@ConditionalOnProperty(name = "twitter-to-kafka-service.enable-mock-tweets", havingValue = "false")
public class StreamRunnerImpl implements StreamRunner {

  private final TwitterToKafkaServiceConfigData configData;
  private final TwitterKafkaStatusListener twitterKafkaStatusListener;

  private TwitterStream twitterStream;

  @Override
  public void start() {
    twitterStream = new TwitterStreamFactory().getInstance();
    twitterStream.addListener(twitterKafkaStatusListener);
    String[] keywords = configData.getTwitterKeywords().toArray(new String[0]);
    FilterQuery filterQuery = new FilterQuery(keywords);
    twitterStream.filter(filterQuery);
    log.info("Started filtering twitter stream for keywords {}", keywords);
  }

  @PreDestroy
  public void shutdown() {
    if (twitterStream != null) {
      log.info("Closing twitter stream...");
      twitterStream.shutdown();
    }
  }
}
