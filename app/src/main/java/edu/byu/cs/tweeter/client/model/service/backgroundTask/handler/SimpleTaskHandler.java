package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.SimpleTaskObserver;

public class SimpleTaskHandler extends BackgroundTaskHandler<SimpleTaskObserver>{

    public SimpleTaskHandler(SimpleTaskObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, SimpleTaskObserver observer) {
        observer.handleSuccess();
    }
}
