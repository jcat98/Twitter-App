package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.GetUserTaskObserver;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.observer.PagedTaskObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter {

    private static final int PAGE_SIZE = 10;

    public interface PagedView<T> extends View{
        void setLoadingFooter(boolean value);

        void addMoreItems(List<T> items);

        void displayUser(User user);
    }

    private PagedView<T> view;

    private User targetUser;

    private AuthToken authToken;

    private T lastItem;

    private boolean hasMorePages;

    private boolean isLoading = false;

    public PagedPresenter(PagedView<T> view) {
        this.view = view;
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void loadMoreItems(User targetUser) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooter(isLoading);
            getItems(targetUser, PAGE_SIZE, lastItem, new GetItemObserver());
        }
    }

    public void getUser(String userAlias) {
        getUserService().getUserProfile(userAlias, new GetUserObserver());
    }

    protected abstract void getItems(User targetUser, int pageSize, T lastItem, GetItemObserver observer);

    protected class GetItemObserver implements PagedTaskObserver<T> {

        @Override
        public void handleSuccess(List<T> items, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(isLoading);

            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addMoreItems(items);
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            view.displayMessage("Failed to get items: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            view.setLoadingFooter(isLoading);
            view.displayMessage("Failed to get items because of exception: " + ex.getMessage());
        }
    }

    private class GetUserObserver implements GetUserTaskObserver {

        @Override
        public void handleSuccess(User user) {
            view.displayUser(user);
            view.displayMessage("Getting user's profile...");
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get user's profile: " + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to get user's profile because of exception: " + ex.getMessage());
        }
    }
}
