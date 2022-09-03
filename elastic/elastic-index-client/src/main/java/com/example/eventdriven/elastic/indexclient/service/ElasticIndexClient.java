package com.example.eventdriven.elastic.indexclient.service;

import com.example.eventdriven.elastic.model.index.IndexModel;

import java.util.List;

public interface ElasticIndexClient<T extends IndexModel> {
    List<String> save(List<T> documents);
}
