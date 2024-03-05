package edu.byu.cs.tweeter.server.dao;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dto.AuthTokenDTO;
import edu.byu.cs.tweeter.server.dto.FollowDTO;
import edu.byu.cs.tweeter.server.service.Utility;
import edu.byu.cs.tweeter.util.Timestamp;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class AuthTokenDynamoDAO implements AuthTokenDAO{
    private static final String TableName = "authtokens";
    public static final String IndexName = "token";

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
    public AuthToken recordAuthToken(String token, long timestamp, String alias) {
        DynamoDbTable<AuthTokenDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthTokenDTO.class));
        Key key = Key.builder()
                .partitionValue(token)
                .build();

        // load it if it exists
        AuthTokenDTO authToken = table.getItem(key);
        if(authToken != null) {
            authToken.setToken(token);
            authToken.setTimestamp(timestamp);
            authToken.setAlias(alias);
            table.updateItem(authToken);
            return authToken.createAuthToken();
        } else {
            AuthTokenDTO newAuthToken = new AuthTokenDTO();
            newAuthToken.setToken(token);
            newAuthToken.setTimestamp(timestamp);
            newAuthToken.setAlias(alias);
            table.putItem(newAuthToken);
            return newAuthToken.createAuthToken();
        }
    }

    @Override
    public String isValidAuthToken(String token, long timestamp) {
        DynamoDbTable<AuthTokenDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthTokenDTO.class));
        Key key = Key.builder()
                .partitionValue(token)
                .build();

        AuthTokenDTO authToken = table.getItem(key);

        if(authToken != null) {
            if(timestamp < authToken.getTimestamp()) {
                long now = System.currentTimeMillis();
                authToken.setTimestamp(now + TimeUnit.MINUTES.toMillis(30));
                table.updateItem(authToken);
                return authToken.getAlias();
            }
        }

        return null;
    }

    @Override
    public boolean deleteAuthToken(String token) {
        DynamoDbTable<AuthTokenDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(AuthTokenDTO.class));
        Key key = Key.builder()
                .partitionValue(token)
                .build();
        table.deleteItem(key);
        return true;
    }
}
