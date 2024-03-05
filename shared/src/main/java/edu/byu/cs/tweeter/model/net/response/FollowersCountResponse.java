package edu.byu.cs.tweeter.model.net.response;

public class FollowersCountResponse extends CountResponse {

    public FollowersCountResponse(boolean success, int count) {
        super(success, count);
    }

    public FollowersCountResponse(boolean success, String message, int count) {
        super(false, message, 0);
    }
}
