package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.CountTaskObserver;

public class CountTaskHandler extends BackgroundTaskHandler<CountTaskObserver>{

    public CountTaskHandler(CountTaskObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, CountTaskObserver observer) {
        int count = data.getInt(GetCountTask.COUNT_KEY);
        observer.handleSuccess(count);
    }
}
