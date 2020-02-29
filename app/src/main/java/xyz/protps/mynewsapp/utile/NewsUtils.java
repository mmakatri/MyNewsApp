package xyz.protps.mynewsapp.utile;



import android.icu.text.SimpleDateFormat;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import xyz.protps.mynewsapp.Model.NewsItem;
import xyz.protps.mynewsapp.data.GuardianConstants;
import xyz.protps.mynewsapp.ui.MainActivity;
import xyz.protps.mynewsapp.data.GuardianConstants.JSONFields;


/**
 * Created by mmakatri on 7/16/2018.
 */
public class NewsUtils {

    //The read timeout for the http url connection
    private static final int READ_TIMEOUT = 10000;

    //The connect timeout for the http url connection
    private static final int CONNECT_TIMEOUT = 15000;

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = NewsUtils.class.getName();

    /**
     * Query the Guardian api and return a list of {@link NewsItem} objects.
     */
    public static List<NewsItem> fetchNews(String requestUrl) {
        // Create URL object
        URL url = createURL(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Extract relevant fields from the JSON response and return this result
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * returns a URL Object from the given String URL
     */
    private static URL createURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        // Create an http connection
        HttpURLConnection urlConnection = null;

        // Create an inputStream to read from the url
        InputStream inputStream = null;
        try {
            // Try to open the connection
            urlConnection = (HttpURLConnection) url.openConnection();

            // Set the timeout for the connection read
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);

            // Set the timeout for the connection
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT /* milliseconds */);

            // Set the request method (GET)
            urlConnection.setRequestMethod("GET");

            // Make the connection
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);

            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {

        // Create an string builder
        StringBuilder output = new StringBuilder();
        // If there is a data
        if (inputStream != null) {
            // Create a new reader with the parameter of the input stream and the charset format
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

            // Create a new buffer reader
            BufferedReader reader = new BufferedReader(inputStreamReader);
            // Read the first line
            String line = reader.readLine();

            // If there is a data, this will be repeated until the last line of the data
            while (line != null) {
                // Add this line to tha string builder
                output.append(line);
                // Read the next line
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * This method is to extract the json data
     *
     * @param newsJSON the data to be extracted
     * @return the newsItem list {@link NewsItem}
     */
    private static List<NewsItem> extractFeatureFromJson(String newsJSON) {

        // If the JSON string is empty just return null
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        //or
        // Create an empty ArrayList that will hold the news items
        List<NewsItem> newsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract the JSONArray associated with the key called "response",
            // which represents a list of results (or news) + header.
            JSONObject response = baseJsonResponse.
                    optJSONObject(GuardianConstants.GUARDIAN_API_RESPONSE);

            // Extract the number of the page currently browsing
            int currentPageId = response.optInt(JSONFields.FIELD_CURRENT_PAGE);

            // Set the value to the activity
            MainActivity.setCurrentPageId(currentPageId);
            // Extract the total amount of pages that are in this call
            int numberOfPages = response.optInt(JSONFields.FIELD_PAGES);

            // Set the value to the activity
            MainActivity.setNumberOfPages(numberOfPages);

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (or news).
            JSONArray newsArray = response.
                    optJSONArray(GuardianConstants.GUARDIAN_API_RESULTS);

            // For each news in the newsArray, create an {@link NewsItem} object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single news at position i within the list of news
                JSONObject currentNews = newsArray.optJSONObject(i);

                // Extract the value for the key called "webTitle"
                String title = currentNews.optString(JSONFields.FIELD_WEB_TITLE);

                // Extract the value for the key called "sectionName"
                String sectionName = currentNews.optString(JSONFields.FIELD_SECTION_NAME);

                // Extract a JSON object that hold the author and thumbnail
                JSONObject fields = currentNews.optJSONObject(JSONFields.FIELD_FIELDS);

                // Create strings variables for author and thumbnail
                String authorFullName = "";
                String thumbnail = "";
                String trailText = "";

                // Check if this object exist
                if (fields != null) {
                    //Extract the value of the key called fields to get the author name
                    authorFullName = fields.optString(JSONFields.FIELD_AUTHOR);

                    //Extract the value of the key called fields to get the thumbnail
                    thumbnail = fields.optString(JSONFields.FIELD_THUMBNAIL);

                    //Extract the value of the key called fields to get the trail text
                    trailText = fields.optString(JSONFields.FIELD_TRAIL_TEXT);
                }

                // Extract the value for the key called "webPublicationDate"
                String date = currentNews.optString(JSONFields.FIELD_DATE);

                //Format publication date
                Date publicationDate = null;

                try {
                    publicationDate = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())).parse(date);
                } catch (Exception e) {
                    // If an error is thrown when executing the above statement in the "try" block,
                    // catch the exception here, so the app doesn't crash. Print a log message
                    // with the message from the exception.
                    e.printStackTrace();
                }

                // Extract the value for the key called "url"
                String url = currentNews.optString(JSONFields.FIELD_WEB_URL);

                // Create a new {@link NewsItem} object with the title, section name, publication date,
                // and url from the JSON response.
                NewsItem news = new NewsItem(title, sectionName, authorFullName, publicationDate, url, thumbnail,trailText);

                // add this object to the list
                newsList.add(news);
            }

            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newsList;
    }

    /**
     * Return the formatted date as string like this ("June 1, 2018") from a Date format.
     */
    public static String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy", Locale.getDefault());
        return dateFormat.format(dateObject);
    }
}
