package edu.byu.cs.tweeter.server.dto;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.AuthTokenDynamoDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
public class AuthTokenDTO {
    private String token;
    private long timestamp;
    private String alias;

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = AuthTokenDynamoDAO.IndexName)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public AuthToken createAuthToken() {
        long now = System.currentTimeMillis();
        return new AuthToken(token, String.valueOf(now));
    }

    @Override
    public String toString() {
        return "AuthToken{" +
                "token='" + token + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", alias='" + alias + '\'' +
                '}';
    }
}
