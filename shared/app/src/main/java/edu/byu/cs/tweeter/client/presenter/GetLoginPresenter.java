package edu.byu.cs.tweeter.client.presenter;

public class GetLoginPresenter extends AuthenticatePresenter {

    public GetLoginPresenter(AuthenticateView view) {
        super(view);
    }

    public void loginUser(String userAlias, String password) {
        getUserService().loginUser(userAlias, password, new AuthenticateObserver());
    }
}
