package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;

public interface FollowDAO {

    boolean recordFollow(String follower_handle, String followee_handle, String follower_name, String followee_name);

    boolean deleteFollow(String follower_handle, String followee_handle);

    boolean isFollower(String follower_handle, String followee_handle);

    Pair<List<String>, Boolean> getFollowees(String targetUserAlias, int pageSize, String lastUserAlias);

    Pair<List<String>, Boolean> getFollowers(String targetUserAlias, int pageSize, String lastUserAlias);
}
