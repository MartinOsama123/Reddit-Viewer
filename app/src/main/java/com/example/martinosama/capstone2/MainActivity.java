package com.example.martinosama.capstone2;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.crash.FirebaseCrash;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MainRecyclerViewAdaptor.ItemClickListener, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private static final String REDIRECT_URI_ROOT = "com.example.martinosama.capstone2";
    private static final String STATE = "nah1";
    private static final String DURATION = "permanent";
    private static final String REDIRECT_URI = "com.example.martinosama.capstone2://www.google.com";
    private static final String CODE = "code";
    private static final String SCOPE = "identity mysubreddits";
    private static final String CLIENT = "RdnNzAz6Ba6tlQ";
    private String code;
    public static ArrayList<String> subreddits;
    private ArrayList<SubRedditInfo> subRedditInfos;
    @BindView(R.id.mainContent)
    RecyclerView recyclerView;
    @BindView(R.id.loginButton)
    Button login;
    @BindView(R.id.warningText)
    TextView warning;
    @BindView(R.id.refreshBtn)
    FloatingActionButton refresh;
    @BindView(R.id.loginCard)
    CardView loginCard;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private MainRecyclerViewAdaptor adaptor;
    public final static String LIST_STATE_KEY = "recycler_list_state";
    Parcelable listState;
    private static Bundle mBundleRecyclerViewState;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        subRedditInfos = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        subreddits = new ArrayList<>();
        adaptor = new MainRecyclerViewAdaptor(getApplicationContext(),subRedditInfos);
        adaptor.setClickListener(this);
        recyclerView.setAdapter(adaptor);
        if(savedInstanceState != null){
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
            subRedditInfos = savedInstanceState.getParcelableArrayList("DATA");
        }
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (!isNetworkAvailable()) {
                    return makeMovementFlags(0, 0);
                } else return makeMovementFlags(0, ItemTouchHelper.RIGHT);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int i = viewHolder.getAdapterPosition();
                //Log.i("Reddit", "Position   = " + i);
                if (direction == ItemTouchHelper.RIGHT) {
                    Retrofit retrofit2 = new Retrofit.Builder().baseUrl("https://www.reddit.com")
                            .build();
                    final RedditClient redditClient = retrofit2.create(RedditClient.class);
                    Call<ResponseBody> call2 = redditClient.getSubRedditInfo(subRedditInfos.get(i).getSubreddit(), subRedditInfos.get(i).getAfter(), 1);
                    call2.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                            try {
                                subRedditInfos.set(i, getInfo(response.body().string(), 1, true));
                            } catch (IOException e) {
                                FirebaseCrash.logcat(Log.INFO, "Reddit", e.getMessage());
                            }
                            adaptor.setDataOnline(subRedditInfos);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            FirebaseCrash.report(t);
                        }
                    });
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        Uri data = getIntent().getData();
        if (isNetworkAvailable()) {
            if (data != null && !TextUtils.isEmpty(data.getScheme())) {
                if (REDIRECT_URI_ROOT.equals(data.getScheme())) {
                    login.setVisibility(View.GONE);
                    warning.setVisibility(View.GONE);
                    loginCard.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    refresh.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    code = data.getQueryParameter(CODE);
                    Log.i("Reddit", "onCreate: handle result of authorization with code :" + code);
                    if (!TextUtils.isEmpty(code)) {
                        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
                            @Override
                            public okhttp3.Response intercept(Chain chain) throws IOException {
                                Request originalRequest = chain.request();

                                Request.Builder builder = originalRequest.newBuilder().header("Authorization",
                                        Credentials.basic(CLIENT, ""));

                                Request newRequest = builder.build();
                                return chain.proceed(newRequest);
                            }
                        }).build();

                        Retrofit retrofit = new Retrofit.Builder()
                                .client(okHttpClient).baseUrl("https://ssl.reddit.com")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        RedditClient redditClient = retrofit.create(RedditClient.class);
                        Call<TokenModel> call = redditClient.getToken("/u/user v1.0", STATE, "identity", CLIENT, REDIRECT_URI, code, "authorization_code");
                        call.enqueue(new Callback<TokenModel>() {
                            @Override
                            public void onResponse(Call<TokenModel> call, retrofit2.Response<TokenModel> response) {
                                TokenModel tokenModel = response.body();
                                final String token = tokenModel.getAccessToken();
                                Log.i("Reddit", token);
                                OkHttpClient httpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
                                    @Override
                                    public okhttp3.Response intercept(Chain chain) throws IOException {
                                        Request originalRequest = chain.request();

                                        Request.Builder builder = originalRequest.newBuilder()
                                                .addHeader("User-Agent", "MockClient/0.1 by Me")
                                                .addHeader("Authorization", "Bearer " + token)
                                                .addHeader("Content-Type", "application/json");

                                        Request newRequest = builder.build();
                                        return chain.proceed(newRequest);
                                    }
                                }).build();
                                Retrofit retrofit1 = new Retrofit.Builder()
                                        .client(httpClient)
                                        .baseUrl("https://oauth.reddit.com")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                RedditClient redditClient = retrofit1.create(RedditClient.class);
                                Call<SubReddits> listCall = redditClient.getSubReddits(500);
                                listCall.enqueue(new Callback<SubReddits>() {
                                    @Override
                                    public void onResponse(Call<SubReddits> call, retrofit2.Response<SubReddits> response) {
                                        SubReddits subReddits = response.body();
                                        int size = subReddits.getData().getDist();
                                        String temp = "";
                                        for (int i = 0; i < size; i++) {
                                            subreddits.add(subReddits.getData().getChildren().get(i).getData().getDisplayName());
                                            temp += "r/" + subreddits.get(i) + "\n";
                                        }

                                        populateOnline("", 0, true);
                                    }

                                    @Override
                                    public void onFailure(Call<SubReddits> call, Throwable t) {
                                        FirebaseCrash.report(t);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Call<TokenModel> call, Throwable t) {

                                FirebaseCrash.report(t);
                            }
                        });
                    }
                }

            } else {
                Cursor cursor = getContentResolver().query(RedditContract.RedditEntry.CONTENT_URI, null, "subreddit", null, null);
                if (cursor.getCount() == 0) {
                    login.setVisibility(View.VISIBLE);
                    warning.setVisibility(View.VISIBLE);
                    loginCard.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                    refresh.setVisibility(View.GONE);
                    login.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getCode();

                        }
                    });
                } else {
                    if (subRedditInfos != null && subRedditInfos.isEmpty()) {
                        cursor.moveToFirst();
                        while (cursor.moveToNext()) {
                            subreddits.add(cursor.getString(cursor.getColumnIndex(RedditContract.RedditEntry.COLUMN_SUBREDDIT_NAME)));
                        }
                        populateOnline("", 0, false);
                    }else{
                        adaptor.setDataOnline(subRedditInfos);
                    }
                }
            }
        } else {
            Toast.makeText(this, R.string.No_Internet, Toast.LENGTH_LONG).show();
            getSupportLoaderManager().initLoader(1, null, this);
        }
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCode();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isNetworkAvailable()) {
                    populateOnline("", 1, true);
                }
                else
                    Toast.makeText(getApplicationContext(), R.string.No_Internet, Toast.LENGTH_LONG).show();
            }
        });
        getIntent().setData(null);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra("PERMALINK", subRedditInfos.get(position).getPermalink());
        i.putExtra("TITLE", subRedditInfos.get(position).getTitle());
        i.putExtra("AUTHOR", subRedditInfos.get(position).getAuthor());
        i.putExtra("SUBREDDIT", subRedditInfos.get(position).getSubreddit());
        i.putExtra("TIME", subRedditInfos.get(position).getCreatedUtc().longValue());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, (View) recyclerView, "profile");
            startActivity(i, options.toBundle());
        } else {
            startActivity(i);
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private SubRedditInfo getInfo(String body, int c, boolean first) {
        SubRedditInfo temp = new SubRedditInfo();
        try {
            JSONObject jsonArray = new JSONObject(body);
            temp.setThumbnail(jsonArray.optJSONObject("data").optJSONArray("children").optJSONObject(0).optJSONObject("data").optString("thumbnail"));
            temp.setTitle(jsonArray.optJSONObject("data").optJSONArray("children").optJSONObject(0).optJSONObject("data").optString("title"));
            temp.setAuthor(jsonArray.optJSONObject("data").optJSONArray("children").optJSONObject(0).optJSONObject("data").optString("author"));
            temp.setCreatedUtc(jsonArray.optJSONObject("data").optJSONArray("children").optJSONObject(0).optJSONObject("data").optDouble("created_utc"));
            temp.setPermalink(jsonArray.optJSONObject("data").optJSONArray("children").optJSONObject(0).optJSONObject("data").optString("permalink"));
            temp.setOver18(jsonArray.optJSONObject("data").optJSONArray("children").optJSONObject(0).optJSONObject("data").optBoolean("over_18"));
            temp.setSubreddit(jsonArray.optJSONObject("data").optJSONArray("children").optJSONObject(0).optJSONObject("data").optString("subreddit"));
            temp.setAfter(jsonArray.optJSONObject("data").optString("after"));
            Cursor cursor = getContentResolver().query(RedditContract.RedditEntry.CONTENT_URI, null, "subreddit", null, null);
            ContentValues cv = new ContentValues();
            cv.put(RedditContract.RedditEntry.COLUMN_SUBREDDIT_THUMBNAIL, temp.getThumbnail());
            cv.put(RedditContract.RedditEntry.COLUMN_SUBREDDIT_18, temp.getOver18());
            cv.put(RedditContract.RedditEntry.COLUMN_SUBREDDIT_AFTER, temp.getAfter());
            cv.put(RedditContract.RedditEntry.COLUMN_SUBREDDIT_AUTHOR, temp.getAuthor());
            cv.put(RedditContract.RedditEntry.COLUMN_SUBREDDIT_NAME, temp.getSubreddit());
            cv.put(RedditContract.RedditEntry.COLUMN_SUBREDDIT_LINK, temp.getPermalink());
            cv.put(RedditContract.RedditEntry.COLUMN_SUBREDDIT_TIME, temp.getCreatedUtc());
            cv.put(RedditContract.RedditEntry.COLUMN_SUBREDDIT_TITLE, temp.getTitle());
            if (c == 0 && first && cursor.getCount()==0) {
                getContentResolver().insert(RedditContract.RedditEntry.CONTENT_URI, cv);
            } else if (c != 0 && first && cursor.getCount() != 0) {
                Uri uri = RedditContract.RedditEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(temp.getSubreddit()).build();
                getContentResolver().update(uri, cv, null, null);
            }
        } catch (JSONException e) {
            FirebaseCrash.log(e.getMessage());
        }
        return temp;
    }

    @NonNull
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        CursorLoader cursorLoader = new CursorLoader(this, RedditContract.RedditEntry.CONTENT_URI, null,
                null, null, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        while (data.moveToNext()) {
            SubRedditInfo subRedditInfo = new SubRedditInfo();
            subRedditInfo.setSubreddit(data.getString(data.getColumnIndex(RedditContract.RedditEntry.COLUMN_SUBREDDIT_NAME)));
            subRedditInfo.setAfter(data.getString(data.getColumnIndex(RedditContract.RedditEntry.COLUMN_SUBREDDIT_AFTER)));
            subRedditInfo.setAuthor(data.getString(data.getColumnIndex(RedditContract.RedditEntry.COLUMN_SUBREDDIT_AUTHOR)));
            subRedditInfo.setPermalink(data.getString(data.getColumnIndex(RedditContract.RedditEntry.COLUMN_SUBREDDIT_LINK)));
            subRedditInfo.setThumbnail(data.getString(data.getColumnIndex(RedditContract.RedditEntry.COLUMN_SUBREDDIT_THUMBNAIL)));
            subRedditInfo.setTitle(data.getString(data.getColumnIndex(RedditContract.RedditEntry.COLUMN_SUBREDDIT_TITLE)));
            subRedditInfo.setOver18(data.getInt(data.getColumnIndex(RedditContract.RedditEntry.COLUMN_SUBREDDIT_18)) == 1);
            subRedditInfo.setCreatedUtc(data.getDouble(data.getColumnIndex(RedditContract.RedditEntry.COLUMN_SUBREDDIT_TIME)));
            subreddits.add(subRedditInfo.getSubreddit());
            subRedditInfos.add(subRedditInfo);
        }

    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {

    }

    private void getCode() {
        HttpUrl authorizeUrl = HttpUrl.parse("https://ssl.reddit.com/api/v1/authorize") //
                .newBuilder()
                .addQueryParameter("state", STATE)
                .addQueryParameter("duration", DURATION)
                .addQueryParameter("response_type", CODE)
                .addQueryParameter("scope", SCOPE)
                .addQueryParameter("client_id", CLIENT)
                .addQueryParameter("redirect_uri", REDIRECT_URI)
                .build();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(String.valueOf(authorizeUrl.url())));
        startActivity(i);
    }

    private void populateOnline(final String afterLink, final int c, final boolean first) {
        subRedditInfos = new ArrayList<>();
        String temp = "";
        swipeRefreshLayout.setRefreshing(true);
        Retrofit retrofit2 = new Retrofit.Builder().baseUrl("https://www.reddit.com")
                .build();
        final RedditClient redditClient = retrofit2.create(RedditClient.class);
        //Log.i("Reddit", "Size" + subreddits.size());
        for (int i = 0; i < subreddits.size(); i++) {
            final String st = subreddits.get(i);
            temp += "r/"+st +"\n";
            Call<ResponseBody> call2 = redditClient.getSubRedditInfo(subreddits.get(i), afterLink, 1);
            call2.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    try {
                        subRedditInfos.add(getInfo(response.body().string(), c, first));
                    } catch (IOException e) {
                        FirebaseCrash.log(e.getMessage());
                    }
                    adaptor.setDataOnline(subRedditInfos);
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    FirebaseCrash.report(t);
                }
            });
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.reddit_widget);
        ComponentName thisWidget = new ComponentName(getApplicationContext(), RedditWidget.class);
        remoteViews.setTextViewText(R.id.appwidget_text, temp);
        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
    }
    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        // Save list state
        listState = recyclerView.getLayoutManager().onSaveInstanceState();
        state.putParcelableArrayList("DATA",subRedditInfos);
        state.putParcelable(LIST_STATE_KEY, listState);
        state.putStringArrayList("SUBREDDITS",subreddits);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        subRedditInfos = savedInstanceState.getParcelableArrayList("DATA");
        listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
        subreddits = savedInstanceState.getStringArrayList("SUBREDDITS");
        super.onRestoreInstanceState(savedInstanceState);
    }

}

