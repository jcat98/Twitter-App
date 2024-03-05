package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.CountTaskObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.IsFollowerTaskObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleTaskObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class GetMainPresenter extends Presenter {

    public interface MainView extends View{

        void updateUsersFollowingAndFollowers(boolean value);

        void enableFollowButton(boolean value);

        void displayFollowing();

        void displayFollow();

        void cancelPostingToast();

        void cancelLogoutToast();

        void logoutUser();

        void displayFollowingCount(int count);

        void displayFollowersCount(int count);

        void displayRemoveMessage();

        void displayAddMessage();
    }

    private MainView view;

    private StatusService statusService;

    private FollowService followService;

    public GetMainPresenter(MainView view) {
        this.view = view;
        this.statusService = getStatusService();
        this.followService = new FollowService();
    }

    public StatusService getStatusService() {
        if (statusService == null) {
            statusService = new StatusService();
        }
        return statusService;
    }

    public void onStatusPosted(String post) {
        System.out.println("in the post status method");
        getStatusService().onStatusPosted(post, new PostStatusObserver());
    }

    public void unfollowUser(User selectedUser) {
        followService.unfollowUser(selectedUser, new UnFollowObserver());
    }

    public void followUser(User selectedUser) {
        followService.followUser(selectedUser, new FollowObserver());
    }

    public void isFollower(User selectedUser) {
        followService.isFollower(selectedUser, new IsFollowerObserver());
    }

    public void logoutUser() {
        getUserService().logoutUser( new LogoutObserver());
    }

    public void clearUserCache() { getUserService().clearUserCache();
    }

    public void updateSelectedUserFollowingAndFollowers(User selectedUser) {
        followService.followingAndFollowersCounts(selectedUser, new FollowingCountObserver(), new FollowersCountObserver());
    }

    private class PostStatusObserver implements SimpleTaskObserver {

        @Override
        public void handleSuccess() {
            System.out.println("in the handle success message for post status");
            view.cancelPostingToast();
            view.displayMessage("Successfully Posted!");
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to post status: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to post status because of exception: " + ex.getMessage());
        }
    }

    private class UnFollowObserver implements SimpleTaskObserver {

        @Override
        public void handleSuccess() {
            view.updateUsersFollowingAndFollowers(true);
            view.enableFollowButton(true);
            view.displayRemoveMessage();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to unfollow: " + message);
            view.enableFollowButton(true);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to unfollow because of exception: " + ex.getMessage());
            view.enableFollowButton(true);
        }
    }

    private class FollowObserver implements SimpleTaskObserver {

        @Override
        public void handleSuccess() {
            view.updateUsersFollowingAndFollowers(false);
            view.enableFollowButton(true);
            view.displayAddMessage();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to follow: " + message);
            view.enableFollowButton(true);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to follow because of exception: " + ex.getMessage());
            view.enableFollowButton(true);
        }
    }

    private class IsFollowerObserver implements IsFollowerTaskObserver {

        @Override
        public void handleSuccess(boolean isFollower) {
            // If logged in user if a follower of the selected user, display the follow button as "following"
            if (isFollower) {
                view.displayFollowing();
            } else {
                view.displayFollow();
            }
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to determine following relationship: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to determine following relationship because of exception: " + ex.getMessage());
        }
    }

    private class FollowingCountObserver implements CountTaskObserver {

        @Override
        public void handleSuccess(int count) {
            view.displayFollowingCount(count);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get following count: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to get following count because of exception: " + ex.getMessage());
        }
    }

    private class FollowersCountObserver implements CountTaskObserver {

        @Override
        public void handleSuccess(int count) {
            view.displayFollowersCount(count);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get followers count: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to get followers count because of exception: " + ex.getMessage());
        }
    }

    private class LogoutObserver implements SimpleTaskObserver {

        @Override
        public void handleSuccess() {
            view.cancelLogoutToast();
            view.logoutUser();
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to logout: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to logout because of exception: " + ex.getMessage());
        }
    }
}
