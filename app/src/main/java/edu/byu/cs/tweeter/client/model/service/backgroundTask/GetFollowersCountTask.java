package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {

    private FollowersCountRequest followersCountRequest;
    private FollowersCountResponse followersCountResponse;

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected int runCountTask() throws IOException, TweeterRemoteException {
        followersCountRequest = new FollowersCountRequest(getAuthToken(), getTargetUser().getAlias());
        followersCountResponse = getServerFacade().getFollowersCount(followersCountRequest, FollowService.URL_PATH_FOLLOWERS_COUNT);

        int count = followersCountResponse.getCount();

        return count;
    }

    @Override
    protected boolean isSuccess() {
        return followersCountResponse.isSuccess();
    }

    @Override
    protected String getMessage() {
        return followersCountResponse.getMessage();
    }
}
