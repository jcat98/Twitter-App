package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthenticatedTask {

    private LogoutRequest logoutRequest;
    private LogoutResponse logoutResponse;

    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
    }

    @Override
    protected void runTask() throws IOException, TweeterRemoteException {
        logoutRequest = new LogoutRequest(getAuthToken());
        logoutResponse = getServerFacade().logout(logoutRequest, UserService.URL_PATH_LOGOUT);

        if(logoutResponse.isSuccess()) {
            sendSuccessMessage();
        }
        else {
            sendFailedMessage(logoutResponse.getMessage());
        }
    }
}
