package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UnfollowRequest {

    private AuthToken authToken;
    private String followeeAlias;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private UnfollowRequest() {
    }

    /**
     * Creates an instance.
     *
     * @param followeeAlias the alias of the user whose followees are to be returned.
     */
    public UnfollowRequest(AuthToken authToken, String followeeAlias) {
        this.authToken = authToken;
        this.followeeAlias = followeeAlias;
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
    public String getFolloweeAlias() {
        return followeeAlias;
    }

    /**
     * Sets the follower.
     *
     * @param followeeAlias the follower.
     */
    public void setFolloweeAlias(String followeeAlias) {
        this.followeeAlias = followeeAlias;
    }
}
