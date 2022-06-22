package com.example.eventdriven.twitter2kafka;

import com.example.eventdriven.twitter2kafka.config.ConfigData;
import com.example.eventdriven.twitter2kafka.runner.StreamRunner;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class TwitterToKafkaServiceApplication implements CommandLineRunner {

  public TwitterToKafkaServiceApplication(ConfigData configData, StreamRunner streamRunner) {
    this.configData = configData;
    this.streamRunner = streamRunner;
  }

  public static void main(String[] args) {
    SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
  }

  private final ConfigData configData;
  private final StreamRunner streamRunner;

  @Override
  public void run(String... args) throws Exception {
    log.info("Starting app...");
    log.info(configData.getTwitterKeywords());
    streamRunner.start();
  }
}
