package com.matheushrs.psp_void_core.service;

import com.matheushrs.psp_void_core.client.ElasticsearchDbClient;
import com.matheushrs.psp_void_core.dto.LogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final ElasticsearchDbClient elasticsearchDb;

    public List<LogResponse> findAll() {
        return elasticsearchDb.findAllLogs();
    }
}
