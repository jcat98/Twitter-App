package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dto.UserDTO;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class UserDynamoDAO implements UserDAO {
    private static final String TableName = "users";
    public static final String IndexName = "alias";

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
    public User recordUser(String firstName, String lastName, String username, String password, String image) {
        DynamoDbTable<UserDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(UserDTO.class));
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        // load it if it exists
        UserDTO user = table.getItem(key);
        if(user != null) {
            user.setFirst_name(firstName);
            user.setLast_name(lastName);
            user.setAlias(username);
            user.setPassword(password);
            user.setImage_url(image);
            table.updateItem(user);
            return user.createUser();
        } else {
            UserDTO newUser = new UserDTO();
            newUser.setFirst_name(firstName);
            newUser.setLast_name(lastName);
            newUser.setAlias(username);
            newUser.setPassword(password);
            newUser.setImage_url(image);
            newUser.setFollower_count(0);
            newUser.setFollowee_count(0);
            table.putItem(newUser);
            return newUser.createUser();
        }
    }

    @Override
    public User getUser(String username) {
        DynamoDbTable<UserDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(UserDTO.class));
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        UserDTO user = table.getItem(key);
        if(user != null) {
            return user.createUser();
        }
        return null;
    }

    public int getFollowersCount(String username) {
        DynamoDbTable<UserDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(UserDTO.class));
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        return table.getItem(key).getFollower_count();
    }

    @Override
    public int getFollowingCount(String username) {
        DynamoDbTable<UserDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(UserDTO.class));
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        return table.getItem(key).getFollowee_count();
    }

    @Override
    public User validateUser(String username, String password) {
        DynamoDbTable<UserDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(UserDTO.class));
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        UserDTO user = table.getItem(key);
        if(user != null) {
            if(user.getPassword().equals(password)) {
                return user.createUser();
            }
        }
        return null;
    }

    @Override
    public List<User> getUsers(List<String> usernames) {
        DynamoDbTable<UserDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(UserDTO.class));

        List<User> users = new ArrayList<>();

        for(String username : usernames) {
            Key key = Key.builder().partitionValue(username).build();
            UserDTO user = table.getItem(key);
            User newUser = user.createUser();
            users.add(newUser);
        }

        return users;
    }

    @Override
    public void followerCount(String username, int count) {
        DynamoDbTable<UserDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(UserDTO.class));
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        UserDTO user = table.getItem(key);
        int followerCount = user.getFollower_count() + count;
        user.setFollower_count(followerCount);
        table.updateItem(user);
    }

    @Override
    public void followeeCount(String username, int count) {
        DynamoDbTable<UserDTO> table = enhancedClient.table(TableName, TableSchema.fromBean(UserDTO.class));
        Key key = Key.builder()
                .partitionValue(username)
                .build();

        UserDTO user = table.getItem(key);
        int followeeCount = user.getFollowee_count() + count;
        user.setFollowee_count(followeeCount);
        table.updateItem(user);
    }
}
