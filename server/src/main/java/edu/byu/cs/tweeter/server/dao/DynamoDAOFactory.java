package edu.byu.cs.tweeter.server.dao;

public class DynamoDAOFactory implements DAOFactory {
    @Override
    public UserDAO getUserDAO() {
        return new UserDynamoDAO();
    }
    @Override
    public FollowDAO getFollowDAO() {
        return new FollowDynamoDAO();
    }

    @Override
    public AuthTokenDAO getAuthTokenDAO() {
        return new AuthTokenDynamoDAO();
    }

    @Override
    public FeedDAO getFeedDAO() {
        return new FeedDynamoDAO();
    }

    @Override
    public StoryDAO getStoryDAO() {
        return new StoryDynamoDAO();
    }

    @Override
    public ImageDAO getImageDAO() {
        return new ImageS3DAO();
    }
}
