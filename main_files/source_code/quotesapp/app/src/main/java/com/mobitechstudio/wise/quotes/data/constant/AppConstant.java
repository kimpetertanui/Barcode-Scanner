package com.mobitechstudio.wise.quotes.data.constant;

public class AppConstant {

    public static final String EMPTY_STRING = "";
    public static final String BUNDLE_KEY_TITLE = "title";
    public static final String BUNDLE_KEY_MESSAGE = "message";
    public static final String BUNDLE_KEY_URL = "url";
    public static final String BUNDLE_FROM_PUSH = "from_push";
    public static final long SITE_CACHE_SIZE = 10 * 1024 * 1024;

    public static final String NEW_NOTI = "new_notification";
    public static final String BUNDLE_KEY_ITEM = "item";
    public static final String BUNDLE_KEY_INDEX = "index";
    public static final String BUNDLE_KEY_YES = "yes";
    public static final String BUNDLE_KEY_NO = "no";
    public static final String BUNDLE_KEY_VIEW_ID = "view_id_tex";
    public static final String BUNDLE_KEY_DIALOG_FRAGMENT = "dialog_fragment";
    public static final String BUNDLE_KEY_EXIT_OPTION = "exit";
    public static final String BUNDLE_KEY_DELETE_EACH_FAV = "delete_each_fav";
    public static final String BUNDLE_KEY_DELETE_ALL_FAV = "delete_all_fav";
    public static final String BUNDLE_KEY_DELETE_ALL_NOT = "delete_all_not";


    public static final int BUNDLE_KEY_ZERO_INDEX = 0;
    public static final int BUNDLE_KEY_FIRST_INDEX = 1;
    public static final int BUNDLE_KEY_HOME_INDEX = 4;
    public static final int BUNDLE_KEY_MAX_INDEX = 10;
    public static final int ADS_INTERVAL = 4;
    public static final int CLICK_COUNT = 3;


    // common for both of the category and author files
    public static final String JSON_KEY_ITEMS = "items";
    public static final String JSON_KEY_QUOTES = "quotes";

    // category file
    public static final String CATEGORY_FILE = "json/categories/category_quotes.json";
    public static final String JSON_KEY_CATEGORY_NAME = "category_name";
    public static final String JSON_KEY_CATEGORY_IMAGE = "category_img";

    // author file
    public static final String AUTHOR_FILE = "json/authors/author_quotes.json";
    public static final String JSON_KEY_AUTHOR_NAME = "author_name";
    public static final String JSON_KEY_AUTHOR_IMAGE = "author_img";


    public static final String TTS_LOCALE = "en_US";

    // text size constants
    public static final int SMALL_TEXT = 14;
    public static final int NORMAL_TEXT = 18;
    public static final int LARGE_TEXT = 22;

    // notification constants
    public static final String PREF_NOTIFICATION = "perf_notification";
    public static final String PREF_FONT_SIZE = "pref_font_size";


}
