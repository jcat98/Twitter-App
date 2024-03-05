package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.AuthenticateTaskObserver;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticatePresenter extends Presenter {

    public interface AuthenticateView extends View {
        void authenticateUser(User registeredUser, String message);
    }

    private AuthenticateView view;

    public AuthenticatePresenter(AuthenticateView view) {
        this.view = view;
    }

    public void validateUser(String alias, String password) {
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    protected class AuthenticateObserver implements AuthenticateTaskObserver {

        @Override
        public void handleSuccess(User user, String message) {
            view.authenticateUser(user, message);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to authenticate: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to authenticate because of exception: " + ex.getMessage());
        }
    }
}
