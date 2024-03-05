package edu.byu.cs.tweeter.client.model.service.backgroundTask.observer;

import edu.byu.cs.tweeter.model.domain.User;

public interface AuthenticateTaskObserver extends ServiceObserver {
    void handleSuccess(User user, String message);
}
