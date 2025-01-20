package com.fiap.fiap_video_extractor.external;

import com.fiap.fiap_video_extractor.core.entities.ExtractionInfo;
import com.fiap.fiap_video_extractor.core.entities.ExtractionInfoFile;
import com.fiap.fiap_video_extractor.core.entities.ExtractionStatus;
import com.fiap.fiap_video_extractor.pkg.interfaces.ExtractionInfoDatasource;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExtractionInfoDynamoDbDatasource implements ExtractionInfoDatasource {

    public static final String EXTRACTION_INFO = "extraction_info";
    private final DynamoDbClient dynamoDbClient;

    public ExtractionInfoDynamoDbDatasource(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    @Override
    public ExtractionInfo save(ExtractionInfo info) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(info.id()).build());
        item.put("user_id", AttributeValue.builder().s(info.userId()).build());
        item.put("status", AttributeValue.builder().s(info.status().name()).build());
        item.put("video_storage_path", AttributeValue.builder().s(info.videoStoragePath()).build());
        item.put("extracted_file_path", AttributeValue.builder().s(info.extractedFilePath()).build());
        item.put("created_at", AttributeValue.builder().s(info.uploadAt().toString()).build());
        item.put("updated_at", AttributeValue.builder().s(info.updatedAt().toString()).build());
        item.put("original_file_name", AttributeValue.builder().s(info.originalFile().name()).build());
        item.put("original_file_size_bytes", AttributeValue.builder().s(info.originalFile().sizeBytes().toString()).build());
        item.put("original_file_hash", AttributeValue.builder().s(info.originalFile().hash()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(EXTRACTION_INFO)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
        return info;
    }

    @Override
    public ExtractionInfo get(String userId, String id) {
        Map<String, AttributeValue> key = Map.of(
                "user_id", AttributeValue.builder().s(userId).build(),
                "id", AttributeValue.builder().s(id).build()
        );

        GetItemRequest request = GetItemRequest.builder()
                .tableName(EXTRACTION_INFO)
                .key(key)
                .build();

        GetItemResponse response = dynamoDbClient.getItem(request);

        if (!response.hasItem()) {
            throw new RuntimeException("No item found for userId: " + userId + " and id: " + id);
        }

        Map<String, AttributeValue> item = response.item();

        return new ExtractionInfo(
                item.get("id").s(),
                item.get("user_id").s(),
                ExtractionStatus.valueOf(item.get("status").s()),
                item.get("video_storage_path").s(),
                item.get("extracted_file_path").s(),
                OffsetDateTime.parse(item.get("created_at").s()),
                OffsetDateTime.parse(item.get("updated_at").s()),
                new ExtractionInfoFile(
                        item.get("original_file_name").s(),
                        Long.parseLong(item.get("original_file_size_bytes").s()),
                        item.get("original_file_hash").s()
                )
        );
    }
}
