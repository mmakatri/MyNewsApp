package xyz.protps.mynewsapp.View;

import java.util.List;

import xyz.protps.mynewsapp.Model.NewsItem;

public interface INewsView {

    /**
     * When the loader finish loading the news from internet
     *
     * @param newsItemList the result from the loader
     */
    void showNews(List<NewsItem> newsItemList);

    // To create the loader (for the 1st time)
    void createTheLoader();

    // To restart the loader (refresh)
    void restartTheLoader();

    // To update the activity title
    void updateTitle();

    // To clear the data from adapter
    void clearAdapter();

    // To show a message to tell the user that there is no connection
    void showNoConnectionView();

    // To show the loading indicator
    void showLoading();

    // Hide the loading indicator
    void hideLoading();

    /**
     * To show a message in the middle of screen when there is no news
     *
     * @param message the message that will be shown
     */
    void showEmptyView(String message);

    // To hide the empty view from screen
    void hideEmptyView();

    // To share the app
    void shareApp();

    // To check the network connection if exist
    boolean checkConnection();
}
