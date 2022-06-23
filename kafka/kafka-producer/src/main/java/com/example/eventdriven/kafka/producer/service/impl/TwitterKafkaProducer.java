package com.example.eventdriven.kafka.producer.service.impl;

import com.example.eventdriven.kafka.avro.model.TwitterAvroModel;
import com.example.eventdriven.kafka.producer.service.KafkaProducer;
import javax.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@RequiredArgsConstructor
public class TwitterKafkaProducer implements KafkaProducer<Long, TwitterAvroModel> {

  private static final Logger log = LoggerFactory.getLogger(TwitterKafkaProducer.class);

  private final KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

  @Override
  public void send(String topicName, Long key, TwitterAvroModel message) {
    log.info("Sending message = '{}' to topic = '{}'", message, topicName);
    //key value indicates target partition of the message
    ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture =
        kafkaTemplate.send(topicName, key, message);

    addCallback(topicName, message, kafkaResultFuture);
  }

  @PreDestroy
  public void close() {
    //ensure kafkaTemplate is shutdown
    if (kafkaTemplate != null) {
      log.info("Closing kafka producer");
      kafkaTemplate.destroy();
    }
  }

  private void addCallback(String topicName, TwitterAvroModel message,
      ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture) {
    kafkaResultFuture.addCallback(
        new ListenableFutureCallback<>() {
          @Override
          public void onFailure(Throwable ex) {
            log.error("Error while sending message {} to topic {}", message, topicName);
          }

          @Override
          public void onSuccess(SendResult<Long, TwitterAvroModel> result) {
            RecordMetadata metadata = result.getRecordMetadata();
            log.debug(
                "Received new metadata. Topic: {}, Partition: {}, Offset: {}, Timestamp {}, at time {}",
                metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp(),
                System.nanoTime());
          }
        });
  }
}
