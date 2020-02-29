package xyz.protps.mynewsapp.ui;

import androidx.loader.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.protps.mynewsapp.BuildConfig;
import xyz.protps.mynewsapp.Model.NewsItem;
import xyz.protps.mynewsapp.Presenter.NewsPresenter;
import xyz.protps.mynewsapp.R;
import xyz.protps.mynewsapp.View.INewsView;
import xyz.protps.mynewsapp.data.GuardianConstants;
import xyz.protps.mynewsapp.data.GuardianConstants.URLQueryFields;
import xyz.protps.mynewsapp.data.GuardianConstants.URLQueryValues;
import xyz.protps.mynewsapp.utile.NewsLoader;

public class MainActivity extends AppCompatActivity
        implements INewsView,
        NewsAdapter.OnItemClickListener,
        NewsAdapter.OnLoadMoreItemClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<List<NewsItem>> {

    // The loader unique ID
    private static final int NEWS_LOADER_ID = 1;
    // Comma separator
    private static final String COMMA_SEP = ",";

    // This variable to hold the index of the item in the navigation drawer
    // we initialize it to 0 , for when we start the app in the beginning
    // always the first item (index 0 ) will be selected.
    private static int selectedItemIndex = 0;
        // A Flag to indicate if the user asking for download more or just the 1st page
    private static boolean loadMore = false;
    // The current page id
    private static int currentPageId = 1;
    // The number of news in this call
    private static int numberOfPages = 1;
    // TextView that is displayed if the list is empty
    @BindView(R.id.empty_view)
    TextView mEmptyView;
    //The loading indicator to show it during downloading news
    @BindView(R.id.loading_indicator)
    View loadingIndicator;
    // The drawer layout
    @BindView(R.id.drawerLayout)
    DrawerLayout drawer;
    // Declaration of the navigation drawer holder
    @BindView(R.id.navigationDrawer)
    NavigationView mNavigationView;
    // Recycler view to hold the news ,find the view in the xml file
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    // Instance from the presenter
    private NewsPresenter newsPresenter;
    // An instance from the news adapter
    private NewsAdapter mNewsAdapter;
    // The section of the news , just a constant id to identify each one
    // the default value is for all news , no particular section
    private String sectionId = GuardianConstants.GUARDIAN_API_SECTION_ALL;
    // Shared Preferences variable
    private SharedPreferences sharedPreferences;

    /**
     * A getter method to get a private state from other context.
     *
     * @return The number of all pages in this call.
     */
    public static int getNumberOfPages() {
        return numberOfPages;
    }

    /**
     * A setter method to set a private state from other context.
     *
     * @param numberOfPages The number of pages returned by the Guardian API.
     */
    public static void setNumberOfPages(int numberOfPages) {
        MainActivity.numberOfPages = numberOfPages;
    }

    /**
     * A getter method to get a private state from other context.
     *
     * @return The id of the current page.
     */
    public static int getCurrentPageId() {
        return currentPageId;
    }

    /**
     * A setter method to set a private state from other context.
     *
     * @param currentPageId The current page id returned by the Guardian API.
     */
    public static void setCurrentPageId(int currentPageId) {
        MainActivity.currentPageId = currentPageId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the views
        ButterKnife.bind(this);

        // Create new instance from presenter attached to this activity view
        newsPresenter = new NewsPresenter(this);

        // Get a reference to the shared Preferences of this app
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Declare and define the custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);

        // Set the custom toolbar for this activity
        setSupportActionBar(toolbar);

        // Configuration for the drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Set the listener for the navigation drawer items
        mNavigationView.setNavigationItemSelectedListener(this);

        // Depend on the recycler view documentation
        // a LayoutManager must be provided for RecyclerView to function
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a new adapter that takes an empty list of news as input
        mNewsAdapter = new NewsAdapter(this, new ArrayList<NewsItem>(), this, this);

        // Set the adapter to the list view
        mRecyclerView.setAdapter(mNewsAdapter);

        // Set the on click listener for the EmptyView ,When there is no result,
        // the user can tap to restart again
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore = false;
                //Call method to restart the loader/
                newsPresenter.onRestartLoader(false);
            }
        });

        // Call the method to select the item from the navigation drawer
        // this is for handle the selection after screen rotation
        onNavigationItemSelected(mNavigationView.getMenu().getItem(selectedItemIndex));

        // Check if the 1st item was selected
        // this is just when we create the activity, to make the item with gray color
        if (selectedItemIndex == 0) {
            // Select the 1st item
            mNavigationView.setCheckedItem(R.id.nav_news);
        }
        // Create a new loader to download the news
        newsPresenter.onCreateLoader();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // To attach this view the presenter
        newsPresenter.setView(this);
    }

    /**
     * To show the news items in the recycler view
     *
     * @param newsItemList the result from the loader
     */
    @Override
    public void showNews(List<NewsItem> newsItemList) {
        if (loadMore) {
            // To add the new data to the end of the list
            mNewsAdapter.addToList(newsItemList);
        } else {
            // To load the new list of news to the adapter
            mNewsAdapter.swapNewData(newsItemList);
        }
    }

    /**
     * This method to check if there is an internet connection
     *
     * @return true if there is a connection
     */
    @Override
    public boolean checkConnection() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        // If there is a network connection
        if (networkInfo != null && networkInfo.isConnected()) {

            // Return true when there is a connection
            return true;

        } else {// Otherwise, display error

            newsPresenter.onNoConnection();
            // And return false
            return false;
        }
    }

    // To show a message to users that there is no connection
    @Override
    public void showNoConnectionView() {

        // Update empty state with no connection error message
        newsPresenter.showEmptyView(getString(R.string.no_connection));
    }

    // Show the loading indicator
    @Override
    public void showLoading() {
        // Show the loading indicator
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    // Hide the loading indicator
    @Override
    public void hideLoading() {
        // Hide loading indicator
        loadingIndicator.setVisibility(View.GONE);
    }

    // Show a view with a message when there is no connection or news
    @Override
    public void showEmptyView(String message) {
        // Set the message to be shown
        mEmptyView.setText(message);
        // Make it visible
        mEmptyView.setVisibility(View.VISIBLE);
    }

    // To hide the empty View that show messages
    @Override
    public void hideEmptyView() {
        // Hide the empty view
        mEmptyView.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        // Check if the drawer is open close it
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else { // or continue with the backPressed
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu xml file
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get the id of the selected menu item
        int itemId = item.getItemId();

        // If the menu item refresh was clicked check if there is a connection
        // then restart the loader with the same id to avoid create a new one
        if (itemId == R.id.refresh) {
            // Just the first page
            loadMore = false;
            // Call method to restart the loader/
            newsPresenter.onRefreshClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        // Get the id of the selected item
        int id = item.getItemId();
        // Get the item index of the selected item, to save it in static variable
        // this is for screen rotation
        selectedItemIndex = getSelectedItemIndex(item);

        // Check witch item was clicked
        switch (id) {
            case R.id.nav_share:
                // Call the method for share the app
                newsPresenter.onShareApp();

                // Close the drawer when user selects a nav item.
                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_settings:
                // Call the method for share the app
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));

                // Close the drawer when user selects a nav item.
                drawer.closeDrawer(GravityCompat.START);
                return true;

            case R.id.nav_news:
                // Set the sectionId to all
                sectionId = GuardianConstants.GUARDIAN_API_SECTION_ALL;
                break;

            case R.id.nav_opinion:
                // Set the sectionId to Opinion section
                sectionId = GuardianConstants.GUARDIAN_API_SECTION_OPINION;
                break;

            case R.id.nav_sport:
                // Set the sectionId to Sport section
                sectionId = GuardianConstants.GUARDIAN_API_SECTION_SPORT;
                break;

            case R.id.nav_culture:
                // Set the sectionId to Culture section
                sectionId = GuardianConstants.GUARDIAN_API_SECTION_CULTURE;
                break;

            case R.id.nav_life_style:
                // Set the sectionId to Life & Style section
                sectionId = GuardianConstants.GUARDIAN_API_SECTION_LIFE_STYLE;
                break;
        }

        // Just load the first page
        loadMore = false;

        // Call method to restart the loader
        newsPresenter.onRestartLoader(false);

        // Close the drawer when user selects a nav item.
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * This method just for restart the loader after checking the connection availability
     */
    @Override
    public void createTheLoader() {
        // Initialize the loader. Pass in the int ID constant defined above
        // and pass in null for the bundle.
        // Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        LoaderManager.getInstance(this).initLoader(NEWS_LOADER_ID, null, this);
    }

    /**
     * This method just for restart the loader after checking the connection availability
     */
    @Override
    public void restartTheLoader() {
        // Restart the loader with the same ID to avoid recreation of a new one.
        LoaderManager.getInstance(this).restartLoader(NEWS_LOADER_ID, null, this);
    }

    // To update the title of the activity
    @Override
    public void updateTitle() {

        // Get the title of the selected item
        String title = mNavigationView.getMenu().
                getItem(selectedItemIndex).getTitle().toString();

        setTitle(title);

    }

    // To clear the adapter
    @Override
    public void clearAdapter() {
        // Set the page to 1 , means we want to load the first page
        currentPageId = 1;

        // Set the page to 0 , this will be updated from JSON data
        numberOfPages = 0;

        // We need just the first page at the beginning
        loadMore = false;

        // Clear the adapter
        mNewsAdapter.clear();
    }

    /**
     * When the loader is created this method will be executed
     *
     * @param id   the loader unique id
     * @param args passing arguments to the loader if needed
     * @return loader with generic type (in my case a list of newsItems)
     */
    @NonNull
    @Override
    public Loader<List<NewsItem>> onCreateLoader(int id, Bundle args) {

        // Get the values from Shared Preferences
        String numberOfNews = sharedPreferences.getString(getString(R.string.settings_number_of_news_key), getString(R.string.settings_number_of_news_default));
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));
        boolean showThumbnail = sharedPreferences.getBoolean(getString(R.string.settings_show_thumbnail_key), true);
        boolean showTrailText = sharedPreferences.getBoolean(getString(R.string.settings_show_trail_text_key), true);

        // Parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GuardianConstants.GUARDIAN_API_URL);

        // BuildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder guardianNewsUri = baseUri.buildUpon();

        //The required URL should be like this:
        //http://content.guardianapis.com/search?q=future&show-fields=byline&order-by=newest&page-size=20&api-key=test

        // Append query parameter and its value.

        guardianNewsUri.appendQueryParameter(
                URLQueryFields.QUERY_Q, URLQueryValues.QUERY_VALUE_FUTURE);

        // Add the author field
        String fields = URLQueryValues.QUERY_VALUE_AUTHOR;

        // If showThumbnail is true add the field thumbnail to the variable fields
        if (showThumbnail) fields += COMMA_SEP + URLQueryValues.QUERY_VALUE_THUMBNAIL;

        // If showTrailText is true add the field trailText to the variable fields
        if (showTrailText) fields += COMMA_SEP + URLQueryValues.QUERY_VALUE_TRAIL_TEXT;

        // To get the author of the article , the thumbnail and the trailText
        guardianNewsUri.appendQueryParameter(URLQueryFields.QUERY_SHOW_FIELDS, fields);

        // To order the result , here I choose the newest,this value should comes from settings
        guardianNewsUri.appendQueryParameter(URLQueryFields.QUERY_ORDER_BY, orderBy);

        // To add the page that we want to load from the API
        guardianNewsUri.appendQueryParameter(
                URLQueryFields.QUERY_PAGE_NUMBER, String.valueOf(currentPageId));

        // To limit how many news in the result,this value should comes from settings
        guardianNewsUri.appendQueryParameter(URLQueryFields.QUERY_PAGE_SIZE, numberOfNews);

        if (!sectionId.equals(GuardianConstants.GUARDIAN_API_SECTION_ALL)) {
            // To limit how many news in the result,this value should comes from settings
            guardianNewsUri.appendQueryParameter(URLQueryFields.QUERY_API_SECTION, sectionId);
        }

        // The key for the api should be included
        guardianNewsUri.appendQueryParameter(
                URLQueryFields.QUERY_API_KEY, BuildConfig.GUARDIAN_API_KEY);


        // Create a new loader for the given URL
        return new NewsLoader(this, guardianNewsUri.toString());
    }
    /**
     * This method will be executed when the loader finish its job
     *
     * @param loader the loader it self with the generic data type
     * @param data   the result of this operation as a list of news
     */
    @Override
    public void onLoadFinished(@NonNull androidx.loader.content.Loader<List<NewsItem>> loader, List<NewsItem> data) {
        // If there is a valid list of NewsItems, then add them to the adapter's
        // data set. This will trigger the recycler view to update.
        if (data != null && !data.isEmpty()) {
            // Handle date ( to show )
            data.size();
            newsPresenter.onLoaderFinish(data);

        } else {
            // If there is no data then show no news found.
            newsPresenter.showEmptyView(getString(R.string.no_news));
        }
    }

    /**
     * When the loader reset we will clear the adapter to load new list
     *
     * @param loader the loader witch will be reset
     */
    @Override
    public void onLoaderReset(@NonNull androidx.loader.content.Loader<List<NewsItem>> loader) {
        // Clear the data from the adapter
        newsPresenter.clearAdapter();
    }

    /**
     * This method for open the news in the browser
     *
     * @param url the news url
     */
    @Override
    public void onItemClick(String url) {
        // Get the url of this item
        Uri newsUri = Uri.parse(url);

        // Create an intent with an action to open this url in the browser
        Intent newsIntent = new Intent(Intent.ACTION_VIEW, newsUri);

        // Check if there is a receiver for this intent
        if (newsIntent.resolveActivity(getPackageManager()) != null) {
            // If yes , send this intent
            startActivity(newsIntent);
        }
    }

    /**
     * This method to share the link of the app using intent with ACTION_SEND
     */
    @SuppressWarnings("deprecation")
    @Override
    public void shareApp() {
        // The app url to share
        String googlePlayAppURL = "https://justDummyUrl.com";

        // Create an intent with an action to send
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        // The data type of the the shared data
        shareIntent.setType("text/plain");

        /* This is from the Intent.java file ( the source code)
        This flag is used to open a document into a new task rooted at the activity launched
        by this Intent. Through the use of this flag, or its equivalent attribute,
        {@link android.R.attr#documentLaunchMode} multiple instances of the same activity
        containing different documents will appear in the recent tasks list.

        The FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET is deprecated since API 21 so the new flag
        is FLAG_ACTIVITY_NEW_DOCUMENT for this reason we check the version of OS
        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }

        // Add the subject of this data
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_this_app));
        // Add the data to be shared
        shareIntent.putExtra(Intent.EXTRA_TEXT, googlePlayAppURL);

        // Check if there is a receiver for this intent
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            // Send the intent using startActivity
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_this_app)));
        }
    }

    /**
     * This is to free the view in the presenter when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        newsPresenter.onDestroy();

    }

    /**
     * This method to get the index (start from 0) of an item in the navigation drawer
     *
     * @param item the selected item in the navigation drawer
     * @return the index of this item
     */
    private int getSelectedItemIndex(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.nav_opinion:
                return 1;

            case R.id.nav_sport:
                return 2;

            case R.id.nav_culture:
                return 3;

            case R.id.nav_life_style:
                return 4;
        }
        // The default is index 0 , the first item (R.id.nav_news)
        return 0;
    }

    /**
     * This method will be called when the user click on the footer of the recycler view
     * to load more news
     */
    @Override
    public void onFooterClick(View v) {

        // Check if there is more pages
        if (currentPageId < numberOfPages) {
            // We disable the view to prevent the user from clicking on it again
            // and we change the text color to inform the user that it is disable
            // it will be enabled again inside the adapter in the binding view
            // and its color will back to normal
            Button loadMoreButton = (Button)v;
            loadMoreButton.setTextColor(Color.GRAY);
            loadMoreButton.setEnabled(false);

            // Put this variable to true to load more news
            loadMore = true;
            // Increment the current page to download the next page
            currentPageId += 1;
            // Restart the loader with the new args
            newsPresenter.onRestartLoader(true);
        }
    }
}
