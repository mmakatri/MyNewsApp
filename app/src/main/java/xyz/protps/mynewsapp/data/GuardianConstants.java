package xyz.protps.mynewsapp.data;

/**
 * Created by mmakatri on 7/21/2018.
 */
public final class GuardianConstants {

    //The base url for the guardian news web site API
    public static final String GUARDIAN_API_URL = "http://content.guardianapis.com/search";

    //Section names in the Guardian API
    public static final String GUARDIAN_API_SECTION_ALL = "all";
    public static final String GUARDIAN_API_SECTION_SPORT = "sport";
    public static final String GUARDIAN_API_SECTION_CULTURE = "culture";
    public static final String GUARDIAN_API_SECTION_LIFE_STYLE = "lifeandstyle";
    public static final String GUARDIAN_API_SECTION_OPINION = "commentisfree";

    //the key word for the json answer data from the guardian API
    //in the object we will find the answer status
    public static final String GUARDIAN_API_RESPONSE = "response";

    //the key word for the json result from the guardian API
    // in this object we will find the list of news
    public static final String GUARDIAN_API_RESULTS = "results";

    //this class hold all constants to create the url for the request
    public static final class URLQueryFields {

        public final static String QUERY_SHOW_FIELDS = "show-fields";
        public final static String QUERY_ORDER_BY = "order-by";
        public final static String QUERY_PAGE_SIZE = "page-size";
        public final static String QUERY_API_KEY = "api-key";
        public final static String QUERY_API_SECTION = "section";
        public final static String QUERY_Q = "q";
        public final static String QUERY_FROM_DATE = "from-date";
        public final static String QUERY_TO_DATE = "to-date";
        public final static String QUERY_PAGE_NUMBER = "page";
    }

    //this class hold all constants to create the url for the request
    public static final class URLQueryValues {

        public final static String QUERY_VALUE_FUTURE = "future";
        public final static String QUERY_VALUE_AUTHOR = "byline";
        public final static String QUERY_VALUE_THUMBNAIL = "thumbnail";
        public final static String QUERY_VALUE_TRAIL_TEXT = "trailText";
    }

    //this class hold all constants in the result data from the guardian api
    public static final class JSONFields {

        public final static String FIELD_WEB_TITLE = "webTitle";
        public final static String FIELD_SECTION_NAME = "sectionName";
        public final static String FIELD_FIELDS = "fields";
        public final static String FIELD_AUTHOR = "byline";
        public final static String FIELD_THUMBNAIL = "thumbnail";
        public final static String FIELD_TRAIL_TEXT = "trailText";
        public final static String FIELD_DATE = "webPublicationDate";
        public final static String FIELD_WEB_URL = "webUrl";

        public final static String FIELD_CURRENT_PAGE = "currentPage";
        public final static String FIELD_PAGES = "pages";
    }
}
