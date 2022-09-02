package com.example.eventdriven.kafka.admin.config;

import com.example.eventdriven.config.KafkaConfigData;
import java.util.Map;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@Configuration
public class KafkaAdminConfig {

  private final KafkaConfigData kafkaConfigData;

  public KafkaAdminConfig(KafkaConfigData kafkaConfigData) {
    this.kafkaConfigData = kafkaConfigData;
  }

  @Bean
  public AdminClient adminClient() {
    return AdminClient.create(
        Map.of(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers())
    );
  }
}
