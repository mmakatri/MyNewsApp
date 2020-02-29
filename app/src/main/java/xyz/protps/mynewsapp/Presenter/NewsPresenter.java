package xyz.protps.mynewsapp.Presenter;

import java.util.List;

import xyz.protps.mynewsapp.Model.NewsItem;
import xyz.protps.mynewsapp.View.INewsView;

public class NewsPresenter implements INewsPresenter {

    // variable to hold the View
    private INewsView iNewsView;

    // Constructor
    public NewsPresenter(INewsView iNewsView) {
        this.iNewsView = iNewsView;
    }

    // Call it when we create the loader for the first time
    @Override
    public void onCreateLoader() {
        // Check if there is a connection
        if (checkConnection()) {
            iNewsView.clearAdapter();

            // Make sure that the textView is hidden
            iNewsView.hideEmptyView();

            // Show the loading indicator
            iNewsView.showLoading();

            // Create the loader
            iNewsView.createTheLoader();
        }
    }

    /**
     * Method to restart the loader
     *
     * @param loadMore Flag to indicate if more news will be loaded or just 1st page
     */
    @Override
    public void onRestartLoader(boolean loadMore) {
        // Check if the connection exist
        if (checkConnection()) {
            // Check if we want to load more news
            if (loadMore) {
                // Show the loading indicator
                iNewsView.showLoading();
                // Restart loader
                iNewsView.restartTheLoader();
            } else {
                // Clear the adapter
                iNewsView.clearAdapter();

                // Make sure that the textView is hidden
                iNewsView.hideEmptyView();

                // Show the loading indicator
                iNewsView.showLoading();

                // Restart loader
                iNewsView.restartTheLoader();
            }
            // Update the activity title
            iNewsView.updateTitle();
        } else {
            iNewsView.showNoConnectionView();
        }
    }

    // Called when the user click on the refresh menu item
    @Override
    public void onRefreshClick() {
        // Check if the connection exist
        if (checkConnection()) {
            // Clear the adapter
            iNewsView.clearAdapter();

            // Make sure that the textView is hidden
            iNewsView.hideEmptyView();

            // Show the loading indicator
            iNewsView.showLoading();

            // When the user click on the refresh icon in the toolbar
            iNewsView.restartTheLoader();
        } else {
            iNewsView.showNoConnectionView();
        }
    }

    // To set the main activity to the view
    @Override
    public void setView(Object view) {
        // Attach the view to this variable (iNewsView)
        iNewsView = (INewsView) view;
    }

    // Called when there is no connection
    @Override
    public void onNoConnection() {
        // Hide the loading indicator
        iNewsView.hideLoading();

        // Show a message to the user that there is no connection
        iNewsView.showNoConnectionView();
    }

    /**
     * When the loader finish loading the news from internet
     *
     * @param newsItemList the result from the loader
     */
    @Override
    public void onLoaderFinish(List<NewsItem> newsItemList) {
        // Hide the loading indicator
        iNewsView.hideLoading();

        // Load the new list of news
        iNewsView.showNews(newsItemList);
    }

    /**
     * To show a message in the middle of screen when there is no news
     *
     * @param message the message that will be shown
     */
    @Override
    public void showEmptyView(String message) {
        // Make sure that the adapter is empty
        iNewsView.clearAdapter();

        // Show the empty view with the desired message
        iNewsView.showEmptyView(message);
    }

    // Called when we want to hide the empty view
    @Override
    public void hideEmptyView() {
        // Hide the empty view either to load the news or to show the loading indicator
        iNewsView.hideEmptyView();
    }

    // Called when we need to clear the adapter to load new list
    @Override
    public void clearAdapter() {
        // Clear the adapter
        if (iNewsView != null)
            iNewsView.clearAdapter();
    }

    // When the activity is destroyed , this will be called to free the view
    @Override
    public void onDestroy() {
        // Free the view
        iNewsView = null;
    }

    // Called when the user want to share the app
    @Override
    public void onShareApp() {
        iNewsView.shareApp();
    }

    /**
     * Called every time we want to check the connection
     *
     * @return true if there is a connection
     */
    @Override
    public boolean checkConnection() {
        return iNewsView.checkConnection();
    }
}
