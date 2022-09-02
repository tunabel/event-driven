package com.example.eventdriven.kafka.admin.client;

import com.example.eventdriven.config.KafkaConfigData;
import com.example.eventdriven.config.RetryConfigData;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class KafkaAdminClient {

  private final KafkaConfigData kafkaConfigData;
  private final AdminClient adminClient;
  private final RetryConfigData retryConfigData;
  private final RetryTemplate retryTemplate;
}
