package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dto.FollowDTO;
import edu.byu.cs.tweeter.server.dto.StoryDTO;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class StoryDynamoDAO implements StoryDAO {

    private static final String TableName = "story";
    public static final String IndexName = "story-index";

    private static final String AliasAttr = "senderAlias";
    private static final String TimestampAttr = "timestamp";

    // DynamoDB client
    private static DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
            .region(Region.US_WEST_1)
            .build();

    private static DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build();

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    @Override
    public boolean recordStory(String senderAlias, long timestamp, Status status) {
        DynamoDbTable<StoryDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(StoryDTO.class));
        Key key = Key.builder()
                .partitionValue(senderAlias).sortValue(timestamp)
                .build();

        // load it if it exists
        StoryDTO story = table.getItem(key);
        if(story != null) {
            story.setSenderAlias(senderAlias);
            story.setTimestamp(timestamp);
            story.setPost(status.post);
            story.setAlias(status.getUser().getAlias());
            story.setFirstName(status.getUser().getFirstName());
            story.setLastName(status.getUser().getLastName());
            story.setImageURL(status.getUser().getImageUrl());
            table.updateItem(story);
        } else {
            StoryDTO newStory = new StoryDTO();
            newStory.setSenderAlias(senderAlias);
            newStory.setTimestamp(timestamp);
            newStory.setPost(status.post);
            newStory.setAlias(status.getUser().getAlias());
            newStory.setFirstName(status.getUser().getFirstName());
            newStory.setLastName(status.getUser().getLastName());
            newStory.setImageURL(status.getUser().getImageUrl());
            table.putItem(newStory);
        }
        return true;
    }

    @Override
    public Pair<List<Status>, Boolean> getStory(String targetUserAlias, int pageSize, Status lastStatus) {
        DynamoDbTable<StoryDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(StoryDTO.class));
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize)
                .scanIndexForward(false);

        if(lastStatus != null) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(AliasAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(TimestampAttr, AttributeValue.builder().n(lastStatus.getTimestamp().toString()).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        List<Status> statuses = new ArrayList<>();
        Pair<List<Status>, Boolean> result = new Pair<>(statuses, true);

        PageIterable<StoryDTO> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<StoryDTO> page) -> {
                    result.setSecond(page.lastEvaluatedKey() != null);
                    page.items().forEach(story -> result.getFirst().add(story.createStatus()));
                });

        return result;
    }
}
