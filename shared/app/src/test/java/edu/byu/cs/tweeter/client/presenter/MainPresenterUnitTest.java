package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleTaskObserver;

public class MainPresenterUnitTest {
    private GetMainPresenter.MainView mockView;
    private StatusService mockStatusService;
    private GetMainPresenter mainPresenterSpy;
    private String status;

    @BeforeEach
    public void setup() {
        // Create Mocks
        mockView = Mockito.mock(GetMainPresenter.MainView.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mainPresenterSpy = Mockito.spy(new GetMainPresenter(mockView));

        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);

        status = "Hello World! I just got some new shoes. Feeling giddy.";
    }

    @Test
    public void testPostStatus_postSuccessful() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String postedStatus = invocation.getArgument(0, String.class);
                SimpleTaskObserver observer = invocation.getArgument(1, SimpleTaskObserver.class);
                observer.handleSuccess();
                assertEquals(status, postedStatus);
                // check if the observer is what it needs to be
                return null;
            }
        };

        mockAndSpyPost(answer);
        verifyMessage(1, "Successfully Posted!");
    }

    @Test
    public void testPostStatus_postFailedWithMessage() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String postedStatus = invocation.getArgument(0, String.class);
                SimpleTaskObserver observer = invocation.getArgument(1, SimpleTaskObserver.class);
                observer.handleFailure("Failed message");
                assertEquals(status, postedStatus);
                return null;
            }
        };

        mockAndSpyPost(answer);
        verifyMessage(0,"Failed to post status: Failed message");
    }

    @Test
    public void testPostStatus_postFailedWithException() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String postedStatus = invocation.getArgument(0, String.class);
                SimpleTaskObserver observer = invocation.getArgument(1, SimpleTaskObserver.class);
                observer.handleException(new Exception("Exception message"));
                assertEquals(status, postedStatus);
                return null;
            }
        };

        mockAndSpyPost(answer);
        verifyMessage(0,"Failed to post status because of exception: Exception message");
    }

    private void mockAndSpyPost(Answer answer) {
        Mockito.doAnswer(answer).when(mockStatusService).onStatusPosted(Mockito.anyString(), Mockito.any());
        mainPresenterSpy.onStatusPosted(status);
    }

    private void verifyMessage(int times, String message) {
        Mockito.verify(mockView, Mockito.times(times)).cancelPostingToast();
        Mockito.verify(mockView).displayMessage(message);
    }
}
