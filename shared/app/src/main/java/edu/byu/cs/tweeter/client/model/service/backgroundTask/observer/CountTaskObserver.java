package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

public interface CountTaskObserver extends ServiceObserver {
    void handleSuccess(int count);
}
