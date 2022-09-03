package com.example.eventdriven.elastic.indexclient.service.impl;

import com.example.eventdriven.config.ElasticConfigData;
import com.example.eventdriven.elastic.indexclient.service.ElasticIndexClient;
import com.example.eventdriven.elastic.indexclient.util.ElasticIndexUtil;
import com.example.eventdriven.elastic.model.index.impl.TwitterIndexModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexedObjectInformation;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
public class TwitterElasticIndexClient implements ElasticIndexClient<TwitterIndexModel> {
    private final ElasticConfigData elasticConfigData;

    private final ElasticsearchOperations elasticsearchOperations;

    private final ElasticIndexUtil<TwitterIndexModel> elasticIndexUtil;

    public TwitterElasticIndexClient(ElasticConfigData configData,
                                     ElasticsearchOperations elasticOperations,
                                     ElasticIndexUtil<TwitterIndexModel> indexUtil) {
        this.elasticConfigData = configData;
        this.elasticsearchOperations = elasticOperations;
        this.elasticIndexUtil = indexUtil;
    }

    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        //convert to indexes
        List<IndexQuery> indexQueries = elasticIndexUtil.getIndexQueries(documents);
        //bulk indexes and save
        List<String> documentIds = elasticsearchOperations
                .bulkIndex(indexQueries, IndexCoordinates.of(elasticConfigData.getIndexName()))
                .stream()
                .map(IndexedObjectInformation::getId)
                .collect(Collectors.toList());
        log.info("Documents indexed successfully with type: {} and ids: {}", TwitterIndexModel.class.getName(),
                documentIds);
        return documentIds;
    }
}
