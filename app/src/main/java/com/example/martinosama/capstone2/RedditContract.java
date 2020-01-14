package com.example.martinosama.capstone2;

import android.net.Uri;
import android.provider.BaseColumns;

public class RedditContract {

    public static final String AUTHORITY = "com.example.martinosama.capstone2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TASKS = "reddit";



    public static final class RedditEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String TABLE_NAME = "reddit";
        public static final String COLUMN_SUBREDDIT_NAME = "subreddit";
        public static final String COLUMN_SUBREDDIT_TITLE = "title";
        public static final String COLUMN_SUBREDDIT_18 = "over_18";
        public static final String COLUMN_SUBREDDIT_AUTHOR = "author";
        public static final String COLUMN_SUBREDDIT_TIME = "created_utc";
        public static final String COLUMN_SUBREDDIT_THUMBNAIL = "thumbnail";
        public static final String COLUMN_SUBREDDIT_LINK = "permalink";
        public static final String COLUMN_SUBREDDIT_AFTER = "afterLink";
    }

}
