package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {

    private StoryRequest storyRequest;
    private StoryResponse storyResponse;

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
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
        storyRequest = new StoryRequest(getAuthToken(), getTargetUser().getAlias(), getLimit(), lastItem);
        storyResponse = getServerFacade().getStory(storyRequest, StatusService.URL_PATH_STORY);

        List<Status> statuses = storyResponse.getStatuses();
        boolean hasMorePages = storyResponse.getHasMorePages();

        return new Pair<>(statuses, hasMorePages);
    }

    @Override
    protected boolean isSuccess() {
        return storyResponse.isSuccess();
    }

    @Override
    protected String getMessage() {
        return storyResponse.getMessage();
    }
}
