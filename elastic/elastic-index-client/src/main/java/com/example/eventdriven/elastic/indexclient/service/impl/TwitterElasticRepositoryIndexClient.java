package com.example.eventdriven.elastic.indexclient.service.impl;

import com.example.eventdriven.elastic.indexclient.repository.TwitterElasticsearchIndexRepository;
import com.example.eventdriven.elastic.indexclient.service.ElasticIndexClient;
import com.example.eventdriven.elastic.model.index.impl.TwitterIndexModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@ConditionalOnProperty(name = "elastic-config.is-repository", havingValue = "true", matchIfMissing = true)
public class TwitterElasticRepositoryIndexClient implements ElasticIndexClient<TwitterIndexModel> {

    private final TwitterElasticsearchIndexRepository twitterElasticsearchIndexRepository;

    public TwitterElasticRepositoryIndexClient(TwitterElasticsearchIndexRepository indexRepository) {
        this.twitterElasticsearchIndexRepository = indexRepository;
    }

    @Override
    public List<String> save(List<TwitterIndexModel> documents) {
        List<TwitterIndexModel> repositoryResponse =
                (List<TwitterIndexModel>) twitterElasticsearchIndexRepository.saveAll(documents);
        List<String> ids = repositoryResponse.stream().map(TwitterIndexModel::getId).collect(Collectors.toList());
        log.info("Documents indexed successfully with type: {} and ids: {}", TwitterIndexModel.class.getName(), ids);
        return ids;
    }
}
