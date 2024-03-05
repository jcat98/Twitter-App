package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

public interface IsFollowerTaskObserver extends ServiceObserver {
    void handleSuccess(boolean isFollower);
}
