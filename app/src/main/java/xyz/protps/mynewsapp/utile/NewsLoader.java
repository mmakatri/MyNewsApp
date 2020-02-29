package xyz.protps.mynewsapp.utile;

import android.content.Context;
import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

import xyz.protps.mynewsapp.Model.NewsItem;


/**
 * Loads a list of news using AsyncTask to perform the network request to the given URL.
 */

 public class NewsLoader extends AsyncTaskLoader<List<NewsItem>> {

    /** Query URL */
    private String mUrl;

    /**
     * Constructor {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url to request the data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        this.mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        // start loading
        forceLoad();
    }

    @Override
    public List<NewsItem> loadInBackground() {
        // Check if there is no url then return.
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of news,
        // And return the list of news
        return NewsUtils.fetchNews(mUrl);
    }
}
