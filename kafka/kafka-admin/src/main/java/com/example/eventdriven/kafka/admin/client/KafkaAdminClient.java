package com.example.eventdriven.kafka.admin.client;

import com.example.eventdriven.config.KafkaConfigData;
import com.example.eventdriven.config.RetryConfigData;
import com.example.eventdriven.kafka.admin.exception.KafkaClientException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
@Log4j2
public class KafkaAdminClient {


  private final KafkaConfigData kafkaConfigData;
  private final AdminClient adminClient;
  private final RetryConfigData retryConfigData;
  private final RetryTemplate retryTemplate;

  private final WebClient webClient;

  public void createTopics() {
    CreateTopicsResult createTopicsResult;
    try {
      //retry until topics are created
      createTopicsResult = retryTemplate.execute(this::doCreateTopics);
      log.info("Create topic result {}", createTopicsResult.values().values());
    } catch (RuntimeException e) {
      throw new KafkaClientException("Reached max number of retries for creating kafka topics");
    }
    //retry until topics can be read
    checkTopicsCreated();
  }

  public void checkTopicsCreated() {
    Collection<TopicListing> topicListings = getTopics();
    int retryCount = 1;
    int maxRetry = retryConfigData.getMaxAttempts();
    int multiplier = retryConfigData.getMultiplier().intValue();
    Long sleepTimeMs = retryConfigData.getSleepTimeMs();

    for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
      while (!isTopicCreated(topicListings, topic)) {
        checkMaxRetry(retryCount++, maxRetry);
        sleep(sleepTimeMs);
        sleepTimeMs *= multiplier;
        topicListings = getTopics();
      }
    }
  }

  public void checkSchemaRegistry() {
    //check schemaRegistry is ready by calling rest
    int retryCount = 1;
    int maxRetry = retryConfigData.getMaxAttempts();
    int multiplier = retryConfigData.getMultiplier().intValue();
    long sleepTimeMs = retryConfigData.getSleepTimeMs();

    while (!getSchemaRegistryStatus().is2xxSuccessful()) {
      checkMaxRetry(retryCount, maxRetry);
      sleep(sleepTimeMs);
      sleepTimeMs *= multiplier;
    }
  }

  private HttpStatus getSchemaRegistryStatus() {
    try {
      HttpStatus httpStatus = webClient.get()
          .uri(kafkaConfigData.getSchemaRegistryUrl())
          .exchange()
          .map(ClientResponse::statusCode)
          .block();
      if (httpStatus != null) {
        return httpStatus;
      }
      return HttpStatus.SERVICE_UNAVAILABLE;
    } catch (Exception e) {
      return HttpStatus.SERVICE_UNAVAILABLE;
    }
  }

  private void sleep(Long sleepTimeMs) {
    try {
      Thread.sleep(sleepTimeMs);
    } catch (InterruptedException e) {
      throw new KafkaClientException("Error while sleeping for waiting new created topics");
    }
  }

  private void checkMaxRetry(int retryCount, Integer maxRetry) {
    if (retryCount > maxRetry) {
      throw new KafkaClientException("Reached max number of retry for reading kafka topics");
    }
  }

  private boolean isTopicCreated(Collection<TopicListing> topicListings, String topic) {
    if (topicListings == null) {
      return false;
    }

    return topicListings.stream().anyMatch(topicListing -> topicListing.name().equals(topic));
  }

  private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
    List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();

    log.info("Creating {} topics, attempt {}", topicNames.size(), retryContext.getRetryCount());
    List<NewTopic> kafkaTopics = topicNames.stream()
        .map(topic -> new NewTopic(
            topic.trim(), //topic name
            kafkaConfigData.getNumOfPartitions(),
            kafkaConfigData.getReplicationFactor()
        )).collect(Collectors.toList());

    //create topics by adminClient
    return adminClient.createTopics(kafkaTopics);
  }

  private Collection<TopicListing> getTopics() {
    Collection<TopicListing> topics;
    try {
      topics = retryTemplate.execute(this::doGetTopics);

    } catch (RuntimeException e) {
      throw new KafkaClientException("Reached max number of retries for reading kafka topics");
    }
    return topics;
  }

  @SneakyThrows
  private Collection<TopicListing> doGetTopics(RetryContext retryContext) {
    log.info("Reading kafka topic {}, attempt {}",
        kafkaConfigData.getTopicNamesToCreate().toArray(), retryContext.getRetryCount());
    Collection<TopicListing> topics = adminClient.listTopics().listings().get();
    if (topics != null) {
      topics.forEach(topic -> log.debug("topic with name {}", topic.name()));
    }
    return topics;
  }
}
