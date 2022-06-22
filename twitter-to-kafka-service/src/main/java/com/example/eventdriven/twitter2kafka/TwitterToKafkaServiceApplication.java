package com.example.eventdriven.twitter2kafka;

import com.example.eventdriven.config.TwitterToKafkaServiceConfigData;
import com.example.eventdriven.twitter2kafka.runner.StreamRunner;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@Log4j2
@ComponentScan(basePackages = "com.example.eventdriven")
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

  public TwitterToKafkaServiceApplication(
      TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData, StreamRunner streamRunner) {
    this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
    this.streamRunner = streamRunner;
  }

  public static void main(String[] args) {
    SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
  }

  private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
  private final StreamRunner streamRunner;

  @Override
  public void run(String... args) throws Exception {
    log.info("Starting app...");
    log.info(twitterToKafkaServiceConfigData.getTwitterKeywords());
    streamRunner.start();
  }
}
