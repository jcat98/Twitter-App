package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {

    private FollowersRequest followersRequest;
    private FollowersResponse followersResponse;

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollower, messageHandler);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() throws IOException, TweeterRemoteException {
//        System.out.println("In the getItems method in the GetFollowersTask");
//        return getFakeData().getPageOfUsers(getLastItem(), getLimit(), getTargetUser());
        String lastItemAlias = null;
        if(getLastItem() != null) {
            lastItemAlias = getLastItem().getAlias();
        }
        followersRequest = new FollowersRequest(getAuthToken(), getTargetUser().getAlias(), getLimit(), lastItemAlias);
//        System.out.println("The GetFollowers request");
//        System.out.println(followersRequest.getAuthToken());
//        System.out.println(followersRequest.getFolloweeAlias());
//        System.out.println(followersRequest.getLimit());
//        System.out.println(followersRequest.getLastFollowerAlias());
        followersResponse = getServerFacade().getFollowers(followersRequest, FollowService.URL_PATH_FOLLOWERS);

        List<User> followees = followersResponse.getFollowers();
        boolean hasMorePages = followersResponse.getHasMorePages();

        return new Pair<>(followees, hasMorePages);
    }

    @Override
    protected boolean isSuccess() {
        return followersResponse.isSuccess();
    }

    @Override
    protected String getMessage() {
        return followersResponse.getMessage();
    }
}
