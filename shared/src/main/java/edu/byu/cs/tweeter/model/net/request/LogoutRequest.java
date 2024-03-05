package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class LogoutRequest {

    private AuthToken authToken;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private LogoutRequest() {}

    /**
     * Creates an instance.
     *
     * @param authToken the username of the user to be logged in.
     */
    public LogoutRequest(AuthToken authToken) {
        this.authToken = authToken;

    }

    /**
     * Returns the username of the user to be logged in by this request.
     *
     * @return the username.
     */
    public AuthToken getAuthToken() {
        return authToken;
    }

    /**
     * Sets the username.
     *
     * @param authToken the username.
     */
    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
