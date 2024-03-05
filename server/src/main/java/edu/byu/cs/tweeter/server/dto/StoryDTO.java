package edu.byu.cs.tweeter.server.dto;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.StoryDynamoDAO;
import edu.byu.cs.tweeter.util.Utility;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class StoryDTO {
    private String senderAlias;
    private long timestamp;
    private String post;
    private String alias;
    private String firstName;
    private String lastName;
    private String imageURL;

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = StoryDynamoDAO.IndexName)
    public String getSenderAlias() {
        return senderAlias;
    }

    public void setSenderAlias(String senderAlias) {
        this.senderAlias = senderAlias;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = StoryDynamoDAO.IndexName)
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Status createStatus() {
        User user = new User(firstName, lastName, alias, imageURL);
        List<String> urls = Utility.parseURLs(post);
        List<String> mentions = Utility.parseMentions(post);

        return new Status(post, user, timestamp, urls, mentions);
    }

    @Override
    public String toString() {
        return "Story{" +
                "senderAlias='" + senderAlias + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", post=" + post +
                ", alias=" + alias +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", imageURL=" + imageURL +
                '}';
    }
}
