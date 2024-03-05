package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class GetFollowingPresenter extends PagedPresenter<User> {

    private FollowService followService;

    public GetFollowingPresenter(PagedView<User> view) {
        super(view);
        followService = new FollowService();
    }

    @Override
    protected void getItems(User targetUser, int pageSize, User lastItem, PagedPresenter<User>.GetItemObserver observer) {
        followService.loadMoreFollowees(targetUser, pageSize, lastItem, observer);
    }
}
