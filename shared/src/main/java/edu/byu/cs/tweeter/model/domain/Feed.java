package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;
import java.util.List;

public class Feed {
    private List<String> followers;
    private Status status;

    /**
     * Allows construction of the object from Json. Private so it won't be called by other code.
     */
    private Feed() {}

    public Feed(List<String> followers, Status status) {
        this.followers = followers;
        this.status = status;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
