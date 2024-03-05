package edu.byu.cs.tweeter.client.model.service;


import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedTaskObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleTaskObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public static final String URL_PATH_FEED = "/getfeed";
    public static final String URL_PATH_STORY = "/getstory";
    public static final String URL_PATH_POST_STATUS = "/poststatus";

    public void loadMoreStatuses(User user, int pageSize, Status lastStatus, PagedTaskObserver<Status> observer) {
        GetStoryTask getStoryTask = new GetStoryTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastStatus, new PagedTaskHandler<Status>(observer));
        BackgroundTaskUtils.runTask(getStoryTask);
    }

    public void loadFeed(User user, int pageSize, Status lastStatus, PagedTaskObserver<Status> observer) {
        GetFeedTask getFeedTask = new GetFeedTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastStatus, new PagedTaskHandler<Status>(observer));
        BackgroundTaskUtils.runTask(getFeedTask);
    }

    public void onStatusPosted(String post, SimpleTaskObserver observer) {
        Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), System.currentTimeMillis(),
                BackgroundTaskUtils.parseURLs(post), BackgroundTaskUtils.parseMentions(post));
        PostStatusTask statusTask = new PostStatusTask(Cache.getInstance().getCurrUserAuthToken(),
                newStatus, new SimpleTaskHandler(observer));
        BackgroundTaskUtils.runTask(statusTask);
    }
}
