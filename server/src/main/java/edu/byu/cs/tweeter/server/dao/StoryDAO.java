package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.util.Pair;

public interface StoryDAO {
    boolean recordStory(String senderAlias, long timestamp, Status status);
    Pair<List<Status>, Boolean> getStory(String targetUserAlias, int pageSize, Status lastStatus);
}
