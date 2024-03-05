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
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {

    private FeedRequest feedRequest;
    private FeedResponse feedResponse;

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() throws IOException, TweeterRemoteException {
//        return getFakeData().getPageOfStatus(getLastItem(), getLimit());
        Status lastItem = null;
        if(getLastItem() != null) {
            lastItem = getLastItem();
        }
        feedRequest = new FeedRequest(getAuthToken(), getTargetUser().getAlias(), getLimit(), lastItem);
        feedResponse = getServerFacade().getFeed(feedRequest, StatusService.URL_PATH_FEED);

        List<Status> statuses = feedResponse.getStatuses();
        boolean hasMorePages = feedResponse.getHasMorePages();

        return new Pair<>(statuses, hasMorePages);
    }

    @Override
    protected boolean isSuccess() {
        return feedResponse.isSuccess();
    }

    @Override
    protected String getMessage() {
        return feedResponse.getMessage();
    }
}
