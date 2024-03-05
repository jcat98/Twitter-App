package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {
    private DAOFactory daoFactory;
    private AuthTokenDAO authTokenDAO;
    private FollowDAO followDAO;
    private UserDAO userDAO;

    public FollowService(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
        this.authTokenDAO = daoFactory.getAuthTokenDAO();
        this.followDAO = daoFactory.getFollowDAO();
        this.userDAO = daoFactory.getUserDAO();
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        String alias = authTokenDAO.isValidAuthToken(request.getAuthToken().getToken(), Long.parseLong(request.getAuthToken().getDatetime()));
        if(alias == null) {
            throw new RuntimeException("[Bad Request] AuthToken has expired");
        }
        Pair<List<String>, Boolean> followees = followDAO.getFollowers(request.getFollowerAlias(),
                request.getLimit(), request.getLastFolloweeAlias());
        List<User> users = userDAO.getUsers(followees.getFirst());
        return new FollowingResponse(users, followees.getSecond());
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        String alias = authTokenDAO.isValidAuthToken(request.getAuthToken().getToken(), Long.parseLong(request.getAuthToken().getDatetime()));
        if(alias == null) {
            throw new RuntimeException("[Bad Request] AuthToken has expired");
        }
        Pair<List<String>, Boolean> followers = followDAO.getFollowees(request.getFolloweeAlias(),
                request.getLimit(), request.getLastFollowerAlias());
        List<User> users = userDAO.getUsers(followers.getFirst());
        return new FollowersResponse(users, followers.getSecond());
    }

    public FollowersCountResponse getCount(FollowersCountRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        }
        String alias = authTokenDAO.isValidAuthToken(request.getAuthToken().getToken(), Long.parseLong(request.getAuthToken().getDatetime()));
        if(alias == null) {
            throw new RuntimeException("[Bad Request] AuthToken has expired");
        }
        int count = userDAO.getFollowingCount(request.getUserAlias());
        return new FollowersCountResponse(true, count);
    }

    public FollowingCountResponse getCount(FollowingCountRequest request) {
        if(request.getUserAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a user alias");
        }
        String alias = authTokenDAO.isValidAuthToken(request.getAuthToken().getToken(), Long.parseLong(request.getAuthToken().getDatetime()));
        if(alias == null) {
            throw new RuntimeException("[Bad Request] AuthToken has expired");
        }
        int count = userDAO.getFollowersCount(request.getUserAlias());
        return new FollowingCountResponse(true, count);
    }

    public FollowResponse follow(FollowRequest request) {
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        String alias = authTokenDAO.isValidAuthToken(request.getAuthToken().getToken(), Long.parseLong(request.getAuthToken().getDatetime()));
        if(alias == null) {
            throw new RuntimeException("[Bad Request] AuthToken has expired");
        }
        User follower = userDAO.getUser(alias);
        User followee = userDAO.getUser(request.getFolloweeAlias());
        boolean followed = followDAO.recordFollow(follower.getAlias(), followee.getAlias(),
                follower.getName(), followee.getName());
        userDAO.followerCount(follower.getAlias(), 1);
        userDAO.followeeCount(followee.getAlias(), 1);
        return new FollowResponse(followed);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        String alias = authTokenDAO.isValidAuthToken(request.getAuthToken().getToken(), Long.parseLong(request.getAuthToken().getDatetime()));
        if(alias == null) {
            throw new RuntimeException("[Bad Request] AuthToken has expired");
        }
        User follower = userDAO.getUser(alias);
        User followee = userDAO.getUser(request.getFolloweeAlias());
        boolean deleted = followDAO.deleteFollow(follower.getAlias(), followee.getAlias());
        userDAO.followerCount(follower.getAlias(), -1);
        userDAO.followeeCount(followee.getAlias(), -1);
        return new UnfollowResponse(deleted);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getAuthToken() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have an authToken");
        } else if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        String alias = authTokenDAO.isValidAuthToken(request.getAuthToken().getToken(), Long.parseLong(request.getAuthToken().getDatetime()));
        if(alias == null) {
            throw new RuntimeException("[Bad Request] AuthToken has expired");
        }
        boolean follow = followDAO.isFollower(request.getFollowerAlias(), request.getFolloweeAlias());
        return new IsFollowerResponse(follow);
    }
}
