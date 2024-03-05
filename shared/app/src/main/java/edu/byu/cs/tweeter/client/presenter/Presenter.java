package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;

public abstract class Presenter {
    public interface View {
        void displayMessage(String message);
    }

    private UserService userService;

    public Presenter() {
        this.userService = new UserService();
    }

    protected UserService getUserService() {
        return userService;
    }
}
