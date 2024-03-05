package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dto.FeedDTO;
import edu.byu.cs.tweeter.server.dto.FollowDTO;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
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


public class FollowDynamoDAO implements FollowDAO {

    private static final String TableName = "follows";
    public static final String IndexName = "follows-index";

    private static final String FollowerAttr = "follower_handle";
    private static final String FolloweeAttr = "followee_handle";

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

    /**
     * Retrieve the follower_name
     *
     * @param followee_handle
     * @param followee_handle
     * @return
     */
    public FollowDTO getFollow(String follower_handle, String followee_handle) {
        DynamoDbTable<FollowDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowDTO.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();

        FollowDTO follow = table.getItem(key);
        return follow;
    }

    /**
     * Increment the number of times visitor has visited location
     *
     * @param follower_handle
     * @param followee_handle
     */
    public boolean recordFollow(String follower_handle, String followee_handle, String follower_name, String followee_name) {
        DynamoDbTable<FollowDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowDTO.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();

        // load it if it exists
        FollowDTO follow = table.getItem(key);
        if(follow != null) {
            follow.setFollower_name(follower_name);
            follow.setFollowee_name(followee_name);
            table.updateItem(follow);
        } else {
            FollowDTO newFollows = new FollowDTO();
            newFollows.setFollower_handle(follower_handle);
            newFollows.setFollowee_handle(followee_handle);
            newFollows.setFollower_name(follower_name);
            newFollows.setFollowee_name(followee_name);
            table.putItem(newFollows);
        }
        return true;
    }

    public boolean isFollower(String follower_handle, String followee_handle) {
        DynamoDbTable<FollowDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowDTO.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();

        // load it if it exists
        FollowDTO follow = table.getItem(key);
        if(follow != null) {
            return true;
        }
        return false;
    }

    /**
     * Delete all visits of visitor to location
     *
     * @param follower_handle
     * @param followee_handle
     */
    public boolean deleteFollow(String follower_handle, String followee_handle) {
        DynamoDbTable<FollowDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowDTO.class));
        Key key = Key.builder()
                .partitionValue(follower_handle).sortValue(followee_handle)
                .build();
        table.deleteItem(key);
        return true;
    }

    /**
     * Fetch the next page of locations visited by visitor
     *
     * @param targetUserAlias The visitor of interest
     * @param pageSize The maximum number of locations to include in the result
     * @param lastUserAlias The last location returned in the previous page of results
     * @return The next page of locations visited by visitor
     */
    public Pair<List<String>, Boolean> getFollowers(String targetUserAlias, int pageSize, String lastUserAlias) {
        DynamoDbTable<FollowDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(FollowDTO.class));
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize)
                .scanIndexForward(false);

        if(isNonEmptyString(lastUserAlias)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FollowerAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(FolloweeAttr, AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        List<String> followers = new ArrayList<>();
        Pair<List<String>, Boolean> result = new Pair<List<String>, Boolean>(followers, true);

        PageIterable<FollowDTO> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowDTO> page) -> {
                    result.setSecond(page.lastEvaluatedKey() != null);
                    page.items().forEach(visit -> result.getFirst().add(visit.getFollowee_handle()));
                });

        return result;
    }

    /**
     * Fetch the next page of visitors who have visited location
     *
     * @param targetUserAlias The location of interest
     * @param pageSize The maximum number of visitors to include in the result
     * @param lastUserAlias The last visitor returned in the previous page of results
     * @return The next page of visitors who have visited location
     */
    public Pair<List<String>, Boolean> getFollowees(String targetUserAlias, int pageSize, String lastUserAlias) {
        DynamoDbIndex<FollowDTO> index = enhancedClient.table(TableName, TableSchema.fromBean(FollowDTO.class)).index(IndexName);
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize)
                .scanIndexForward(true);

        if(isNonEmptyString(lastUserAlias)) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(FolloweeAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(FollowerAttr, AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        List<String> followers = new ArrayList<>();
        Pair<List<String>, Boolean> result = new Pair<List<String>, Boolean>(followers, true);

        SdkIterable<Page<FollowDTO>> sdkIterable = index.query(request);
        PageIterable<FollowDTO> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowDTO> page) -> {
                    result.setSecond(page.lastEvaluatedKey() != null);
                    page.items().forEach(visit -> result.getFirst().add(visit.getFollower_handle()));
                });

        return result;
    }
}
