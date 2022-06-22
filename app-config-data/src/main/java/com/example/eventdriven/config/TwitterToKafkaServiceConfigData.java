package com.example.eventdriven.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "twitter-to-kafka-service")
public class TwitterToKafkaServiceConfigData {

  private List<String> twitterKeywords;
  private Boolean enableMockTweets;
  private Long mockSleepMs;
  private Integer mockMinTweetLength;
  private Integer mockMaxTweetLength;
}
