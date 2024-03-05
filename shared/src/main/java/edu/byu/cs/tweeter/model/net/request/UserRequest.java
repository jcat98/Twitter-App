package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UserRequest {

    private AuthToken authToken;
    private String userAlias;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private UserRequest() {
    }

    /**
     * Creates an instance.
     *
     * @param userAlias the alias of the user whose followees are to be returned.
     */
    public UserRequest(AuthToken authToken, String userAlias) {
        this.authToken = authToken;
        this.userAlias = userAlias;
    }

    /**
     * Returns the auth token of the user who is making the request.
     *
     * @return the auth token.
     */
    public AuthToken getAuthToken() {
        return authToken;
    }

    /**
     * Sets the auth token.
     *
     * @param authToken the auth token.
     */
    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    /**
     * Returns the follower whose followees are to be returned by this request.
     *
     * @return the follower.
     */
    public String getUserAlias() {
        return userAlias;
    }

    /**
     * Sets the follower.
     *
     * @param userAlias the follower.
     */
    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }
}
