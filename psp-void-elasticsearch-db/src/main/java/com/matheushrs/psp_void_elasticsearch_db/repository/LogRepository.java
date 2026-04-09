package com.matheushrs.psp_void_elasticsearch_db.repository;

import com.matheushrs.psp_void_elasticsearch_db.document.LogDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface LogRepository extends ElasticsearchRepository<LogDocument, String> {

    List<LogDocument> findByOrigin(String origin);
}
