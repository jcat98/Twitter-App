package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusRequest {

    private AuthToken authToken;
    private Status status;

    /**
     * Allows construction of the object from Json. Private so it won't be called in normal code.
     */
    private PostStatusRequest() {}

    /**
     * Creates an instance.
     *
     * @param status the alias of the user whose followees are to be returned.
     */
    public PostStatusRequest(AuthToken authToken, Status status) {
        this.authToken = authToken;
        this.status = status;
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
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the follower.
     *
     * @param status the follower.
     */
    public void setStatus(Status status) {
        this.status = status;
    }
}
