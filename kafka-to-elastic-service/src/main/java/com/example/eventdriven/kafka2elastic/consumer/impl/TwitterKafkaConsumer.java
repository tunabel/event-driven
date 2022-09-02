package com.example.eventdriven.kafka2elastic.consumer.impl;

import com.example.eventdriven.config.KafkaConfigData;
import com.example.eventdriven.config.KafkaConsumerConfigData;
import com.example.eventdriven.kafka.admin.client.KafkaAdminClient;
import com.example.eventdriven.kafka.avro.model.TwitterAvroModel;
import com.example.eventdriven.kafka2elastic.consumer.KafkaConsumer;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Log4j2
@Service
public class TwitterKafkaConsumer implements KafkaConsumer<Long, TwitterAvroModel> {
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private final KafkaAdminClient kafkaAdminClient;

    private final KafkaConfigData kafkaConfigData;

    private final KafkaConsumerConfigData kafkaConsumerConfigData;

    public TwitterKafkaConsumer(KafkaListenerEndpointRegistry listenerEndpointRegistry,
                                KafkaAdminClient adminClient,
                                KafkaConfigData configData,
                                KafkaConsumerConfigData consumerConfigData) {
        this.kafkaListenerEndpointRegistry = listenerEndpointRegistry;
        this.kafkaAdminClient = adminClient;
        this.kafkaConfigData = configData;
        this.kafkaConsumerConfigData = consumerConfigData;
    }

    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {
        kafkaAdminClient.checkTopicsCreated();
        log.info("Topics with name {} is ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
        MessageListenerContainer mlc = kafkaListenerEndpointRegistry
                .getListenerContainer(kafkaConsumerConfigData.getConsumerGroupId());
        Objects.requireNonNull(mlc);
        mlc.start();
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.consumer-group-id}", topics = "${kafka-config.topic-name}")
    public void receive(@Payload List<TwitterAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of message received with keys {}, partitions {} and offsets {}, " +
                        "sending it to elastic: Thread id {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId());
    }
}
