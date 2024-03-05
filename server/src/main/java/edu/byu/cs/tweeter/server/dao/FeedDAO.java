package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public interface FeedDAO {
    boolean recordFeed(List<String> followersAlias, long timestamp, Status status);
    Pair<List<Status>, Boolean> getFeed(String targetUserAlias, int pageSize, Status lastStatus);

    void addStatusToFeedBatch(List<String> followers, Status status, Long timeStamp);
}
