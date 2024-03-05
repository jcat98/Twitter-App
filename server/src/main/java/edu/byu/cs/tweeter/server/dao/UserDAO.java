package edu.byu.cs.tweeter.server.dao;


import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public interface UserDAO {
    User recordUser(String firstName, String lastName, String username, String password, String image);
    User getUser(String username);
    User validateUser(String username, String password);
    int getFollowersCount(String username);
    int getFollowingCount(String username);

    List<User> getUsers(List<String> usernames);

    void followerCount(String username, int count);

    void followeeCount(String username, int count);
}
