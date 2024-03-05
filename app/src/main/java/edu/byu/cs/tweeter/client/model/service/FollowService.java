package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.CountTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.CountTaskObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.IsFollowerTaskObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedTaskObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleTaskObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public static final String URL_PATH_FOLLOWING = "/getfollowing";
    public static final String URL_PATH_FOLLOWERS = "/getfollowers";
    public static final String URL_PATH_FOLLOWERS_COUNT = "/getfollowerscount";
    public static final String URL_PATH_FOLLOWING_COUNT = "/getfollowingcount";
    public static final String URL_PATH_FOLLOW = "/follow";
    public static final String URL_PATH_UNFOLLOW = "/unfollow";
    public static final String URL_PATH_IS_FOLLOWER = "/isfollower";

    public void loadMoreFollowees(User user, int pageSize, User lastFollow, PagedTaskObserver<User> observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollow, new PagedTaskHandler<User>(observer));
        BackgroundTaskUtils.runTask(getFollowingTask);
    }

    public void loadMoreFollowers(User user, int pageSize, User lastFollower, PagedTaskObserver<User> observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollower, new PagedTaskHandler<User>(observer));
        BackgroundTaskUtils.runTask(getFollowersTask);
    }

    public void unfollowUser(User selectedUser, SimpleTaskObserver observer) {
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new SimpleTaskHandler(observer));
        BackgroundTaskUtils.runTask(unfollowTask);
    }

    public void followUser(User selectedUser, SimpleTaskObserver observer) {
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new SimpleTaskHandler(observer));
        BackgroundTaskUtils.runTask(followTask);
    }

    public void isFollower(User selectedUser, IsFollowerTaskObserver observer) {
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), selectedUser, new IsFollowerHandler(observer));
        BackgroundTaskUtils.runTask(isFollowerTask);
    }

    public void followingAndFollowersCounts(User selectedUser, CountTaskObserver followingObserver,
                                            CountTaskObserver followersObserver) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new CountTaskHandler(followersObserver));
        executor.execute(followersCountTask);

        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new CountTaskHandler(followingObserver));
        executor.execute(followingCountTask);
    }
}
