package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;

    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }

    @Override
    protected Pair<User, AuthToken> runAuthenticationTask() throws IOException, TweeterRemoteException {
        loginRequest = new LoginRequest(username, password);
        loginResponse = getServerFacade().login(loginRequest, UserService.URL_PATH_LOGIN);

        User loggedInUser = loginResponse.getUser();
        AuthToken authToken = loginResponse.getAuthToken();

        return new Pair<>(loggedInUser, authToken);
    }

    @Override
    protected boolean isSuccess() {
        return loginResponse.isSuccess();
    }

    @Override
    protected String getMessage() {
        return loginResponse.getMessage();
    }
}
