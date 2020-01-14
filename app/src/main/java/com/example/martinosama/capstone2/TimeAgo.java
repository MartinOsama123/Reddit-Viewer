package com.example.martinosama.capstone2;

import android.content.Context;

public class TimeAgo {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static String getTimeAgo(long time,Context context) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }


        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return context.getResources().getString(R.string.just_now);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.minute_ago);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.minutes_ago,diff / MINUTE_MILLIS);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return context.getResources().getString(R.string.hour_ago);
        } else if (diff < 24 * HOUR_MILLIS) {
            return context.getResources().getString(R.string.hours_ago,diff / HOUR_MILLIS);
        } else if (diff < 48 * HOUR_MILLIS) {
            return context.getResources().getString(R.string.yesterday);
        } else {
            return context.getResources().getString(R.string.days_ago,diff / DAY_MILLIS);
        }
    }
}
