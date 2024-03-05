package edu.byu.cs.tweeter.client.model.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleTaskObserver;
import edu.byu.cs.tweeter.client.presenter.GetMainPresenter;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class StatusServiceTest2 {

    private User currentUser;
    private AuthToken currentAuthToken;

    private GetMainPresenter.MainView mainActivityMock;
    private GetMainPresenter mainPresenterSpy;

    private ServerFacade serverFacade;

    private CountDownLatch countDownLatch;

    /**
     * Create a StatusService spy that uses a mock ServerFacade to return known responses to
     * requests.
     */
    @BeforeEach
    public void setup() {
        try {
            LoginRequest loginRequest = new LoginRequest("@ariggs", "ilovejosh");
            serverFacade = new ServerFacade();
            LoginResponse loginResponse = serverFacade.login(loginRequest, "/login");
            currentUser = loginResponse.getUser();
            currentAuthToken = loginResponse.getAuthToken();
            Cache.getInstance().setCurrUser(currentUser);
            Cache.getInstance().setCurrUserAuthToken(currentAuthToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mainActivityMock = Mockito.mock(MainActivity.class);
        mainPresenterSpy = Mockito.spy(new GetMainPresenter(mainActivityMock));

        Mockito.when(mainPresenterSpy.getPostStatusObserver()).thenReturn(new PostStatusObserver());

        // Prepare the countdown latch
        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    /**
     * A {@link PostStatusObserver} implementation that can be used to get the values
     * eventually returned by an asynchronous call on the {@link FollowService}. Counts down
     * on the countDownLatch so tests can wait for the background thread to call a method on the
     * observer.
     */
    private class PostStatusObserver implements SimpleTaskObserver {

        @Override
        public void handleSuccess() {
            mainActivityMock.cancelPostingToast();
            mainActivityMock.displayMessage("Successfully Posted!");

            countDownLatch.countDown();
        }

        @Override
        public void handleFailure(String message) {
            mainActivityMock.displayMessage("Failed to post status: " + message);

            countDownLatch.countDown();
        }

        @Override
        public void handleException(Exception ex) {
            mainActivityMock.displayMessage("Failed to post status because of exception: " + ex.getMessage());

            countDownLatch.countDown();
        }
    }

    /**
     * Verify that for successful requests, the {@link StatusService#loadMoreStatuses}
     * asynchronous method eventually returns the same result as the {@link ServerFacade}.
     */
    @Test
    public void testOnMoreStatuses_validRequest_correctResponse() throws InterruptedException {
        String post = "I love my wonderful husband";
        mainPresenterSpy.onStatusPosted(post);

        awaitCountDownLatch();

        // check if posting the status was successfully
        Mockito.verify(mainActivityMock, Mockito.times(1)).cancelPostingToast();
        Mockito.verify(mainActivityMock, Mockito.times(1)).displayMessage("Successfully Posted!");

        // get the users story
        StoryResponse response = null;
        try {
            StoryRequest request = new StoryRequest(currentAuthToken, currentUser.getAlias(),
                    10, null);
            response = serverFacade.getStory(request, "/getstory");
        } catch (IOException | TweeterRemoteException e) {
            throw new RuntimeException(e);
        }

        // excepted lists
        List<String> urls = new ArrayList<>();
        List<String> mentions = new ArrayList<>();

        // status from aws
        Status recentStatus = response.getStatuses().get(0);

        // assertions
        Assertions.assertEquals(post, recentStatus.getPost());
        Assertions.assertEquals(currentUser, recentStatus.getUser());
        Assertions.assertEquals(urls, recentStatus.getUrls());
        Assertions.assertEquals(mentions, recentStatus.getMentions());
    }
}
