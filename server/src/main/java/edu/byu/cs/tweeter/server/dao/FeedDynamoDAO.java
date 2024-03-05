package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dto.FeedDTO;
import edu.byu.cs.tweeter.server.dto.StoryDTO;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.BatchWriteResult;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.WriteBatch;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class FeedDynamoDAO implements FeedDAO {
    private static final String TableName = "feed";
    public static final String IndexName = "feed-index";

    private static final String AliasAttr = "followerAlias";
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
    public boolean recordFeed(List<String> followersAlias, long timestamp, Status status) {
        DynamoDbTable<FeedDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(FeedDTO.class));
        for(String follower : followersAlias) {
            Key key = Key.builder()
                    .partitionValue(follower).sortValue(timestamp)
                    .build();

            // load it if it exists
            FeedDTO feed = table.getItem(key);
            if(feed != null) {
                feed.setFollowerAlias(follower);
                feed.setTimestamp(timestamp);
                feed.setPost(status.post);
                feed.setAlias(status.getUser().getAlias());
                feed.setFirstName(status.getUser().getFirstName());
                feed.setLastName(status.getUser().getLastName());
                feed.setImageURL(status.getUser().getImageUrl());
                table.updateItem(feed);
            } else {
                FeedDTO newFeed = new FeedDTO();
                newFeed.setFollowerAlias(follower);
                newFeed.setTimestamp(timestamp);
                newFeed.setPost(status.post);
                newFeed.setAlias(status.getUser().getAlias());
                newFeed.setFirstName(status.getUser().getFirstName());
                newFeed.setLastName(status.getUser().getLastName());
                newFeed.setImageURL(status.getUser().getImageUrl());
                table.putItem(newFeed);
            }
        }
        return true;
    }

    @Override
    public Pair<List<Status>, Boolean> getFeed(String targetUserAlias, int pageSize, Status lastStatus) {
        DynamoDbTable<FeedDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(FeedDTO.class));
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

        PageIterable<FeedDTO> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<FeedDTO> page) -> {
                    result.setSecond(page.lastEvaluatedKey() != null);
                    page.items().forEach(feed -> result.getFirst().add(feed.createStatus()));
                });

        return result;
    }

    public void addStatusToFeedBatch(List<String> followees, Status status, Long timestamp) {
        List<FeedDTO> batchToWrite = new ArrayList<>();
        for (String followee : followees) {
            FeedDTO feedDTO = new FeedDTO();
            feedDTO.setFollowerAlias(followee);
            feedDTO.setTimestamp(timestamp);
            feedDTO.setPost(status.getPost());
            feedDTO.setAlias(status.getUser().getAlias());
            feedDTO.setFirstName(status.getUser().getFirstName());
            feedDTO.setLastName(status.getUser().getLastName());
            feedDTO.setImageURL(status.getUser().getImageUrl());
            batchToWrite.add(feedDTO);

            if (batchToWrite.size() == 25) {
                // package this batch up and send to DynamoDB.
                writeChunkOfUserDTOs(batchToWrite);
                batchToWrite = new ArrayList<>();
            }
        }

        // write any remaining
        if (batchToWrite.size() > 0) {
            // package this batch up and send to DynamoDB.
            writeChunkOfUserDTOs(batchToWrite);
        }
    }
    private void writeChunkOfUserDTOs(List<FeedDTO> feedDTOs) {
        if(feedDTOs.size() > 25)
            throw new RuntimeException("Too many users to write");

        DynamoDbTable<FeedDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(FeedDTO.class));
        WriteBatch.Builder<FeedDTO> writeBuilder = WriteBatch.builder(FeedDTO.class).mappedTableResource(table);
        for (FeedDTO item : feedDTOs) {
            writeBuilder.addPutItem(builder -> builder.item(item));
        }
        BatchWriteItemEnhancedRequest batchWriteItemEnhancedRequest = BatchWriteItemEnhancedRequest.builder()
                .writeBatches(writeBuilder.build()).build();

        try {
            BatchWriteResult result = enhancedClient.batchWriteItem(batchWriteItemEnhancedRequest);

            // just hammer dynamodb again with anything that didn't get written this time
            if (result.unprocessedPutItemsForTable(table).size() > 0) {
                writeChunkOfUserDTOs(result.unprocessedPutItemsForTable(table));
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
