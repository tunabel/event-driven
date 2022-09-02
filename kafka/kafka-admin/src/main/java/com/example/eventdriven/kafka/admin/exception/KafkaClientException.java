package com.example.eventdriven.kafka.admin.exception;

public class KafkaClientException extends RuntimeException {

  public KafkaClientException() {
    super();
  }

  public KafkaClientException(String message) {
    super(message);
  }
}
