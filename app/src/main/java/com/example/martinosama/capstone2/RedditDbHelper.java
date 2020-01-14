package com.example.martinosama.capstone2;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class RedditDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "redditDB.db";

    private static final int DATABASE_VERSION = 3;

    public RedditDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + RedditContract.RedditEntry.TABLE_NAME + " (" +
                RedditContract.RedditEntry.COLUMN_SUBREDDIT_NAME + " TEXT PRIMARY KEY, " +
                RedditContract.RedditEntry.COLUMN_SUBREDDIT_TITLE + " TEXT NOT NULL, " +
                RedditContract.RedditEntry.COLUMN_SUBREDDIT_18 + " INTEGER DEFAULT 0, " +
                RedditContract.RedditEntry.COLUMN_SUBREDDIT_AFTER + " TEXT NOT NULL, " +
                RedditContract.RedditEntry.COLUMN_SUBREDDIT_LINK +" TEXT NOT NULL, "+
                RedditContract.RedditEntry.COLUMN_SUBREDDIT_AUTHOR +" TEXT NOT NULL, "+
                RedditContract.RedditEntry.COLUMN_SUBREDDIT_THUMBNAIL +" TEXT NOT NULL, "+
                RedditContract.RedditEntry.COLUMN_SUBREDDIT_TIME +" DOUBLE NOT NULL "+
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RedditContract.RedditEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}