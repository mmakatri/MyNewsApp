package xyz.protps.mynewsapp.Presenter;

import java.util.List;

import xyz.protps.mynewsapp.Model.NewsItem;


/**
 * Created by mmakatri on 7/29/2018.
 */
interface INewsPresenter {
    // To create the loader for the first time
    void onCreateLoader();

    // To restart the loader
    void onRestartLoader(boolean loadMore);

    // To refresh the list of news
    void onRefreshClick();

    // To attach the view to the presenter
    void setView(Object view);

    // To show a message to tell the user that there is no connection
    void onNoConnection();

    /**
     * When the loader finish loading the news from internet
     *
     * @param newsItemList the result from the loader
     */
    void onLoaderFinish(List<NewsItem> newsItemList);

    /**
     * To show a message in the middle of screen when there is no news
     *
     * @param message the message that will be shown
     */
    void showEmptyView(String message);

    // To hide the empty view from screen
    void hideEmptyView();

    // To clear the data from adapter
    void clearAdapter();

    // To free the view when the activity destroyed
    void onDestroy();

    // To share the app
    void onShareApp();

    // To check the network connection if exist
    boolean checkConnection();

}
