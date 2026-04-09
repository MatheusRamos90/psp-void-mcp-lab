package com.matheushrs.psp_void_elasticsearch_db.document;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.DateFormat;

import java.time.Instant;

@Document(indexName = "logs")
@Data
@Builder
public class LogDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String trace;

    @Field(type = FieldType.Keyword)
    private String origin;

    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private Instant createdAt;
}
