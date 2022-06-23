package com.example.eventdriven.kafka.producer.config;

import com.example.eventdriven.config.KafkaConfigData;
import com.example.eventdriven.config.KafkaProducerConfigData;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig<K extends Serializable, V extends SpecificRecordBase> {

  private final KafkaConfigData kafkaConfigData;
  private final KafkaProducerConfigData kafkaProducerConfigData;

  public KafkaProducerConfig(KafkaConfigData kafkaConfigData,
      KafkaProducerConfigData kafkaProducerConfigData) {
    this.kafkaConfigData = kafkaConfigData;
    this.kafkaProducerConfigData = kafkaProducerConfigData;
  }

  @Bean
  public Map<String, Object> producerConfig() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
    configs.put(kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl());
    configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        kafkaProducerConfigData.getKeySerializerClass());
    configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        kafkaProducerConfigData.getValueSerializerClass());
    configs.put(ProducerConfig.BATCH_SIZE_CONFIG,
        kafkaProducerConfigData.getBatchSize() * kafkaProducerConfigData.getBatchSizeBoostFactor());
    configs.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfigData.getLingerMs());
    configs.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,
        kafkaProducerConfigData.getCompressionType());
    configs.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfigData.getAcks());
    configs.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,
        kafkaProducerConfigData.getRequestTimeoutMs());
    configs.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerConfigData.getRetryCount());
    return configs;
  }

  @Bean
  public ProducerFactory<K, V> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfig());
  }

  @Bean
  public KafkaTemplate<K, V> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }
}
