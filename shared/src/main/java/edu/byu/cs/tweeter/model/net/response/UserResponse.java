package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.User;

public class UserResponse extends Response {
    private User user;

    public UserResponse(User user) {
        super(true);
        this.user = user;
    }

    public UserResponse(boolean success, String message, User user) {
        super(success, message);
        this.user = user;
    }

    /**
     * An indicator of whether more data is available from the server. A value of true indicates
     * that the result was limited by a maximum value in the request and an additional request
     * would return additional data.
     *
     * @return true if more data is available; otherwise, false.
     */
    public User getUser() {
        return user;
    }
}
