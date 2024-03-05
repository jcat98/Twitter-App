package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {

    private FollowingCountRequest followingCountRequest;
    private FollowingCountResponse followingCountResponse;

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected int runCountTask() throws IOException, TweeterRemoteException {
        followingCountRequest = new FollowingCountRequest(getAuthToken(), getTargetUser().getAlias());
        followingCountResponse = getServerFacade().getFollowingCount(followingCountRequest, FollowService.URL_PATH_FOLLOWING_COUNT);

        int count = followingCountResponse.getCount();

        return count;
    }

    @Override
    protected boolean isSuccess() {
        return followingCountResponse.isSuccess();
    }

    @Override
    protected String getMessage() {
        return followingCountResponse.getMessage();
    }
}
