package com.example.eventdriven.twitter2kafka;

import com.example.eventdriven.twitter2kafka.init.StreamInitializer;
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

  private final StreamRunner streamRunner;
  private final StreamInitializer streamInitializer;

  public TwitterToKafkaServiceApplication(StreamRunner streamRunner,
      StreamInitializer streamInitializer) {
    this.streamRunner = streamRunner;
    this.streamInitializer = streamInitializer;
  }

  public static void main(String[] args) {
    SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    log.info("Starting app...");
    streamInitializer.init();
    streamRunner.start();
  }
}
