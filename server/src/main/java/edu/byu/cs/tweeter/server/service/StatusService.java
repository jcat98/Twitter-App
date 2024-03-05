package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Feed;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.ion.SystemSymbols;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class StatusService {

    private DAOFactory daoFactory;
    private AuthTokenDAO authTokenDAO;
    private StoryDAO storyDAO;
    private FeedDAO feedDAO;
    private UserDAO userDAO;
    private Utility utility;
    private FollowDAO followDAO;

    public StatusService(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
        this.authTokenDAO = daoFactory.getAuthTokenDAO();
        this.storyDAO = daoFactory.getStoryDAO();
        this.feedDAO = daoFactory.getFeedDAO();
        this.userDAO = daoFactory.getUserDAO();
        this.followDAO = daoFactory.getFollowDAO();
        this.utility = new Utility();
    }

    public FeedResponse getFeed(FeedRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        String alias = authTokenDAO.isValidAuthToken(request.getAuthToken().getToken(), Long.parseLong(request.getAuthToken().getDatetime()));
        if(alias == null) {
            throw new RuntimeException("[Bad Request] AuthToken has expired");
        }
        Pair<List<Status>, Boolean> statuses = feedDAO.getFeed(request.getUserAlias(), request.getLimit(), request.getLastStatus());
        return new FeedResponse(statuses.getFirst(), statuses.getSecond());
    }

    public StoryResponse getStory(StoryRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        String alias = authTokenDAO.isValidAuthToken(request.getAuthToken().getToken(), Long.parseLong(request.getAuthToken().getDatetime()));
        if(alias == null) {
            throw new RuntimeException("[Bad Request] AuthToken has expired");
        }
        Pair<List<Status>, Boolean> statuses = storyDAO.getStory(request.getUserAlias(), request.getLimit(), request.getLastStatus());
        return new StoryResponse(statuses.getFirst(), statuses.getSecond());
    }

    public PostStatusResponse getStatus(PostStatusRequest request) {
        if(request.getStatus() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        }
        String alias = authTokenDAO.isValidAuthToken(request.getAuthToken().getToken(), Long.parseLong(request.getAuthToken().getDatetime()));
        if(alias == null) {
            throw new RuntimeException("[Bad Request] AuthToken has expired");
        }
        // post the status the users story
        boolean posted = storyDAO.recordStory(alias, utility.currentTimestamp(), request.getStatus());

        postStatusMessages(request);

        return new PostStatusResponse(posted);
    }

    public void getFolloweesForFeed(PostStatusRequest request) {
        Status status = request.getStatus();
        User user = status.getUser();
        // get the users follower count
        int count = userDAO.getFollowingCount(user.getAlias());
        System.out.println("Followers count");
        System.out.println(count);

        if(count > 0) {
            String lastUserAlias = null;
            int followeesRemaining = count;
            while (followeesRemaining > 0) {
                // get a list of follower aliases
                Pair<List<String>, Boolean> follows = followDAO.getFollowees(user.getAlias(), 25, lastUserAlias);
                List<String> followees = follows.getFirst();

                System.out.println("Size of the followees list");
                System.out.println(followees.size());

                // create new feed object
                Feed feed = new Feed(followees, status);
                // sqs call
                updateFeed(feed);

                // update lastUSerAlias
                lastUserAlias = followees.get(followees.size() - 1);
                // update hasMorePages
                followeesRemaining -= 25;
                System.out.println(lastUserAlias);
                System.out.println(followeesRemaining);
            }
        }

        System.out.println("Leaving the get followers for feed function");
    }

    private void postStatusMessages(PostStatusRequest request) {
        edu.byu.cs.tweeter.util.Utility utilityShared = new edu.byu.cs.tweeter.util.Utility();

        String messageBody = utilityShared.serialize(request);
        String queueUrl = "https://sqs.us-west-1.amazonaws.com/969740778625/PostStatusMessages";

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(messageBody);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        sqs.sendMessage(send_msg_request);
    }

    public void postStatusToFeed(Feed feed) {
        System.out.println("posting status to feed");
        List<String> followees = feed.getFollowers();
        System.out.println(followees.size());
        System.out.println(followees.get(followees.size() - 1));
        feedDAO.addStatusToFeedBatch(feed.getFollowers(), feed.getStatus(), utility.currentTimestamp());
    }

    private void updateFeed(Feed feed) {
        edu.byu.cs.tweeter.util.Utility utilityShared = new edu.byu.cs.tweeter.util.Utility();

        String messageBody = utilityShared.serialize(feed);
        String queueUrl = "https://sqs.us-west-1.amazonaws.com/969740778625/UpdateFeeds";

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(messageBody);

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        sqs.sendMessage(send_msg_request);
    }
}
