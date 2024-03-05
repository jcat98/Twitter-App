package edu.byu.cs.tweeter.server.dto;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.FollowDynamoDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class FollowDTO {
    private String follower_handle;
    private String followee_handle;

    private String follower_name;

    private String followee_name;

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = FollowDynamoDAO.IndexName)
    public String getFollower_handle() {
        return follower_handle;
    }

    public void setFollower_handle(String handle) {
        this.follower_handle = handle;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = FollowDynamoDAO.IndexName)
    public String getFollowee_handle() {
        return followee_handle;
    }

    public void setFollowee_handle(String handle) {
        this.followee_handle = handle;
    }

    public String getFollower_name() {
        return follower_name;
    }

    public void setFollower_name(String name) {
        this.follower_name = name;
    }

    public String getFollowee_name() {
        return followee_name;
    }

    public void setFollowee_name(String name) {
        this.followee_name = name;
    }

    public Follow createFollow(User follower, User followee) {
        return new Follow(follower, followee);
    }

    @Override
    public String toString() {
        return "Follows{" +
                "follower_handle='" + follower_handle + '\'' +
                ", followee_handle='" + followee_handle + '\'' +
                ", follower_name=" + follower_name +
                ", followee_name=" + followee_name +
                '}';
    }
}
