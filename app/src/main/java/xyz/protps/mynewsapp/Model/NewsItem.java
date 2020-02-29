package xyz.protps.mynewsapp.Model;

import java.util.Date;

import xyz.protps.mynewsapp.utile.NewsUtils;

/**
 * Created by mmakatri on 7/16/2018.
 */
public class NewsItem {

    //new section of this article
    private String mNewsSection;

    //news title
    private String mNewsTitle;

    //news trial text
    private String mNewsTrailText;

    //news time and date
    private Date mNewsTimeAndDate;

    //author of article
    private String mNewsAuthor;

    //URL to that contains the news data
    private String mNewsUrl;

    //URL to that contains the news thumbnail
    private String mNewsThumbnail;

    /**
     * constructs an new object
     *
     * @param title           is the title of the news article
     * @param sectionName     is the header of the news article
     * @param authorFullName  is the authors who wrote the news article
     * @param publicationDate is the time date the news article was published
     * @param url             is the url of the news article
     * @param thumbnail       is the url of the news article thumbnail
     */

    public NewsItem(String title, String sectionName, String authorFullName, Date publicationDate, String url, String thumbnail, String trailText) {

        mNewsSection = sectionName;
        mNewsTitle = title;
        mNewsAuthor = authorFullName;
        mNewsTimeAndDate = publicationDate;
        mNewsUrl = url;
        mNewsThumbnail = thumbnail;
        mNewsTrailText = trailText;
    }

    //getters for all states of this object

    //getter to return the news section
    public String getNewsSection() {
        return mNewsSection;
    }

    //getter to return the news title
    public String getNewsTitle() {
        return mNewsTitle;
    }

    //getter to return the news time and date
    public String getNewsDate() {
        // we check if it is not nul ,format it else return nothing
        if (mNewsTimeAndDate != null)
            return NewsUtils.formatDate(mNewsTimeAndDate);
        return "";
    }

    //getter to return the news author
    public String getNewsAuthor() {
        return mNewsAuthor;
    }

    //getter to return the news trial text
    public String getNewsTrailText() {
        return mNewsTrailText;
    }

    //getter to return the news url
    public String getNewsUrl() {
        return mNewsUrl;
    }

    //getter to return the news thumbnail url
    public String getNewsThumbnail() {
        return mNewsThumbnail;
    }
}
