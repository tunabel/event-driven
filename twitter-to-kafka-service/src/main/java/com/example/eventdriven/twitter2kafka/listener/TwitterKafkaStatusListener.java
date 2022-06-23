package com.example.eventdriven.twitter2kafka.listener;

import com.example.eventdriven.config.KafkaConfigData;
import com.example.eventdriven.kafka.avro.model.TwitterAvroModel;
import com.example.eventdriven.kafka.producer.service.KafkaProducer;
import com.example.eventdriven.twitter2kafka.transformer.TwitterStatusToAvroTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;

@Log4j2
@Component
@RequiredArgsConstructor
public class TwitterKafkaStatusListener extends StatusAdapter {

  private final KafkaConfigData kafkaConfigData;
  private final KafkaProducer<Long, TwitterAvroModel> kafkaProducer;
  private final TwitterStatusToAvroTransformer twitterStatusToAvroTransformer;

  @Override
  public void onStatus(Status status) {
    log.info("Received status with text {} sending to kafka topic {}", status.getText(),
        kafkaConfigData.getTopicName());
    TwitterAvroModel twitterAvroModel = twitterStatusToAvroTransformer.getTwitterAvroModelFromStatus(
        status);
    //use userId as key so that msgs of same user go to same partition
    kafkaProducer.send(kafkaConfigData.getTopicName(), twitterAvroModel.getUserId(),
        twitterAvroModel);
  }

}
