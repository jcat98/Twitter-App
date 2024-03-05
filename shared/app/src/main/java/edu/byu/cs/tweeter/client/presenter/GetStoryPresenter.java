package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class GetStoryPresenter extends PagedPresenter<Status> {

    private StatusService statusService;

    public GetStoryPresenter(PagedView<Status> view) {
        super(view);
        statusService = new StatusService();
    }

    @Override
    protected void getItems(User user, int pageSize, Status lastItem, PagedPresenter<Status>.GetItemObserver observer) {
        statusService.loadMoreStatuses(user, pageSize, lastItem, observer);
    }
}
