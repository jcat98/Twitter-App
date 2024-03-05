package edu.byu.cs.tweeter.client.model.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class ServerFacadeTest {

    private ServerFacade serverFacade;

    @BeforeEach
    public void setup() {
        serverFacade = new ServerFacade();
    }

    @Test
    public void RegisterTestPass() {
        RegisterRequest request = new RegisterRequest("Josh", "Tidwell", "@jcat98",
                "password", "/pathtoimage");
        RegisterResponse response = null;

        try {
            response = serverFacade.register(request, "/register");
        } catch (Exception ex) {
            //keep the response null if an exception happens
        }

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getAuthToken());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals("Allen", response.getUser().getFirstName());
        Assertions.assertEquals("Anderson", response.getUser().getLastName());
        Assertions.assertEquals("@allen", response.getUser().getAlias());
        Assertions.assertEquals("https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png",
                response.getUser().getImageUrl());
        Assertions.assertEquals("Allen Anderson", response.getUser().getName());
    }

    @Test
    public void RegisterTestFail() {
        RegisterRequest request = new RegisterRequest(null, "Tidwell", "@jcat98",
                "password", "/pathtoimage");
        RegisterResponse response = null;

        try {
            response = serverFacade.register(request, "/register");
        } catch (Exception ex) {
            //keep the response null if an exception happens
        }

        Assertions.assertNotNull(response);
        Assertions.assertFalse(response.isSuccess());
    }

    @Test
    public void GetFollowersTestPass() {
        FollowersRequest request = new FollowersRequest(new AuthToken(), "@jcat98", 3, null);
        FollowersResponse response = null;

        try {
            response = serverFacade.getFollowers(request, "/getfollowers");
        } catch (Exception ex) {
            //keep the response null if an exception happens
        }

        List<User> expectedFollowers = FakeData.getInstance().getFakeUsers().subList(0, 3);

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.getHasMorePages());
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertNotNull(response.getFollowers());
        Assertions.assertEquals(expectedFollowers.size(), response.getFollowers().size());

        for(int i = 0; i < 3; i++) {
            User expectedUser = expectedFollowers.get(i);
            User actualUser = response.getFollowers().get(i);
            Assertions.assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
            Assertions.assertEquals(expectedUser.getLastName(), actualUser.getLastName());
            Assertions.assertEquals(expectedUser.getAlias(), actualUser.getAlias());
            Assertions.assertEquals(expectedUser.getImageUrl(), actualUser.getImageUrl());
            Assertions.assertEquals(expectedUser.getName(), actualUser.getName());
        }
    }

    @Test
    public void GetFollowersTestFail() {
        FollowersRequest request = new FollowersRequest(new AuthToken(), null, 10, null);
        FollowersResponse response = null;

        try {
            response = serverFacade.getFollowers(request, "/getfollowers");
        } catch (Exception ex) {
            //keep the response null if an exception happens
        }

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertFalse(response.getHasMorePages());
    }

    @Test
    public void GetFollowersCountTestPass() {
        FollowersCountRequest request = new FollowersCountRequest(new AuthToken(), "@jcat98");
        FollowersCountResponse response = null;

        try {
            response = serverFacade.getFollowersCount(request, "/getfollowerscount");
        } catch (Exception ex) {
            //keep the response null if an exception happens
        }

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.isSuccess());
        Assertions.assertEquals(20, response.getCount());
    }

    @Test
    public void GetFollowersCountTestFail() {
        FollowersCountRequest request = new FollowersCountRequest(new AuthToken(), null);
        FollowersCountResponse response = null;

        try {
            response = serverFacade.getFollowersCount(request, "/getfollowerscount");
        } catch (Exception ex) {
            //keep the response null if an exception happens
        }

        Assertions.assertFalse(response.isSuccess());
        Assertions.assertEquals(0, response.getCount());
    }
}


