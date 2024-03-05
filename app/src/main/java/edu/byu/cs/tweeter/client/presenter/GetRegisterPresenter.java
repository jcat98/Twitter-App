package edu.byu.cs.tweeter.client.presenter;

import android.graphics.drawable.Drawable;

public class GetRegisterPresenter extends AuthenticatePresenter {

    public GetRegisterPresenter(AuthenticateView view) {
        super(view);
    }

    public void registerUser(String firstName, String lastName, String alias, String password, Drawable imageToUpload) {
        getUserService().registerUser(firstName, lastName, alias, password, imageToUpload, new AuthenticateObserver());
    }

    public void validateRegistration(String firstName, String lastName, String alias, String password,
                                Drawable imageToUpload) {
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (imageToUpload == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
        validateUser(alias, password);
    }
}
