package com.example.eventdriven.twitter2kafka.init.impl;

import com.example.eventdriven.config.KafkaConfigData;
import com.example.eventdriven.kafka.admin.client.KafkaAdminClient;
import com.example.eventdriven.twitter2kafka.init.StreamInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class KafkaStreamInitializerImpl implements StreamInitializer {

  private final KafkaConfigData kafkaConfigData;
  private final KafkaAdminClient kafkaAdminClient;

  @Override
  public void init() {
    kafkaAdminClient.createTopics();
    kafkaAdminClient.checkSchemaRegistry();
    log.info("Topics with name {} nare ready for operation.",
        kafkaConfigData.getTopicNamesToCreate());
  }
}
