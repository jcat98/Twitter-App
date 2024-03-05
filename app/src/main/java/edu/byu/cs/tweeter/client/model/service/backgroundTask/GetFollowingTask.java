package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedUserTask {

    private FollowingRequest followingRequest;
    private FollowingResponse followingResponse;


    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollowee, messageHandler);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() throws IOException, TweeterRemoteException {
        String lastItemAlias = null;
        if(getLastItem() != null) {
            lastItemAlias = getLastItem().getAlias();
        }
        followingRequest = new FollowingRequest(getAuthToken(), getTargetUser().getAlias(), getLimit(), lastItemAlias);
        followingResponse = getServerFacade().getFollowees(followingRequest, FollowService.URL_PATH_FOLLOWING);

        List<User> followees = followingResponse.getFollowees();
        boolean hasMorePages = followingResponse.getHasMorePages();

        return new Pair<>(followees, hasMorePages);
    }

    @Override
    protected boolean isSuccess() {
        return followingResponse.isSuccess();
    }

    @Override
    protected String getMessage() {
        return followingResponse.getMessage();
    }
}
