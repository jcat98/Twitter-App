package edu.byu.cs.tweeter.server.dto;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.UserDynamoDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
public class UserDTO {
    private String first_name;
    private String last_name;
    private String alias;
    private String password;
    private String image_url;
    private int follower_count;
    private int followee_count;

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = UserDynamoDAO.IndexName)
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String name) {
        this.first_name = name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String name) {
        this.last_name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String url) {
        this.image_url = url;
    }

    public int getFollower_count() {
        return follower_count;
    }

    public void setFollower_count(int follower_count) {
        this.follower_count = follower_count;
    }

    public int getFollowee_count() {
        return followee_count;
    }

    public void setFollowee_count(int followee_count) {
        this.followee_count = followee_count;
    }

    public User createUser() {
        return new User(first_name, last_name, alias, image_url);
    }

    @Override
    public String toString() {
        return "User{" +
                "first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", alias=" + alias +
                ", password=" + password +
                ", image_url=" + image_url +
                ", follower_count=" + follower_count +
                ", followee_count=" + followee_count +
                '}';
    }
}
