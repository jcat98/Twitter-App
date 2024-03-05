package edu.byu.cs.tweeter.server.dao;

public interface DAOFactory {
    UserDAO getUserDAO();
    FollowDAO getFollowDAO();
    AuthTokenDAO getAuthTokenDAO();
    FeedDAO getFeedDAO();
    StoryDAO getStoryDAO();

    ImageDAO getImageDAO();
}
