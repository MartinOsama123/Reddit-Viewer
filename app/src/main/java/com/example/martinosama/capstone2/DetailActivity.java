package com.example.martinosama.capstone2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.postTitle) TextView textView;
    @BindView(R.id.detailImage) ImageView imageView;
    @BindView(R.id.simplePlayer) PlayerView simpleExoPlayerView;
    @BindView(R.id.subredditPost) TextView subredditPost;
    @BindView(R.id.authorPost) TextView authorPost;
    @BindView(R.id.link) TextView hyperLink;
    @BindView(R.id.recyclerComments) RecyclerView recyclerView;
    @BindView(R.id.download) Button downloadBtn;
    private SimpleExoPlayer player = null;
    private List<String> comments,authors;
    private CommentsAdaptor adaptor;
    String URL= null;
    Bundle bundle;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        comments = new ArrayList<>();
        authors = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adaptor = new CommentsAdaptor(getApplicationContext(), comments,authors);
        recyclerView.setAdapter(adaptor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        bundle = getIntent().getExtras();
        textView.setText(bundle.getString("TITLE"));
        subredditPost.setText(getResources().getString(R.string.subreddit,bundle.getString("SUBREDDIT")));
        authorPost.setText(getResources().getString(R.string.posted_by,bundle.getString("AUTHOR") , TimeAgo.getTimeAgo(bundle.getLong("TIME"),this)));
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.reddit.com").build();
        RedditClient redditClient = retrofit.create(RedditClient.class);
        Call<ResponseBody> call = redditClient.getPostInfo(bundle.getString("PERMALINK")+".json");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                   URL = jsonArray.optJSONObject(0).optJSONObject("data").optJSONArray("children").optJSONObject(0).optJSONObject("data").optString("url");
                   boolean isReddit = jsonArray.optJSONObject(0).optJSONObject("data").optJSONArray("children").optJSONObject(0).optJSONObject("data").optBoolean("is_reddit_media_domain");
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(FirebaseAnalytics.Param.ITEM_ID, bundle.getString("PERMALINK"));
                    if(isReddit)
                    bundle1.putString(FirebaseAnalytics.Param.ITEM_NAME, "reddit");
                    bundle1.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "site");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                   for(int i = 0;i<jsonArray.optJSONObject(1).optJSONObject("data").optJSONArray("children").length();i++){
                       String comment = jsonArray.optJSONObject(1).optJSONObject("data").optJSONArray("children").optJSONObject(i).optJSONObject("data").optString("body");
               //        Log.i("Reddit","Comments: "+comment);
                       comments.add(jsonArray.optJSONObject(1).optJSONObject("data").optJSONArray("children").optJSONObject(i).optJSONObject("data").optString("body"));
                       authors.add(jsonArray.optJSONObject(1).optJSONObject("data").optJSONArray("children").optJSONObject(i).optJSONObject("data").optString("author") + " " + TimeAgo.getTimeAgo(jsonArray.optJSONObject(1).optJSONObject("data").optJSONArray("children").optJSONObject(i).optJSONObject("data").optLong("created_utc"),getApplicationContext()));
                       adaptor.setData(comments,authors);
                   }
             //      Log.i("Reddit","URL = "+URL);
                    hyperLink.setText(URL);
                    final String finalURL = URL;
                    hyperLink.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(finalURL));
                            startActivity(i);
                        }
                    });
                   if(isReddit) {
                       if (URL.endsWith(".jpg") || URL.endsWith(".png")) {
                           imageView.setVisibility(View.VISIBLE);
                           downloadBtn.setVisibility(View.VISIBLE);
                           simpleExoPlayerView.setVisibility(View.GONE);
                           imageView.setContentDescription(getResources().getString(R.string.image,bundle.getString("SUBREDDIT")));
                           GlideApp.with(getApplicationContext()).load(URL).fitCenter().placeholder(R.drawable.reddit).into(imageView);

                       } else if (URL.endsWith(".gif") || URL.endsWith(".gifv")) {
                           if (URL.endsWith(".gifv"))
                               URL = URL.substring(0, URL.length() - 1);
                           imageView.setVisibility(View.VISIBLE);
                           downloadBtn.setVisibility(View.GONE);
                           simpleExoPlayerView.setVisibility(View.GONE);
                           GlideApp.with(getApplicationContext()).asGif().load(URL).placeholder(R.drawable.reddit).into(imageView);

                       }
                       else {

                           imageView.setVisibility(View.GONE);
                           downloadBtn.setVisibility(View.GONE);
                           simpleExoPlayerView.setVisibility(View.VISIBLE);
                           preparingVideo(URL + "/DASHPlaylist.mpd");
                           player.setPlayWhenReady(true);
                       }
                   }else{
                       imageView.setVisibility(View.GONE);
                       downloadBtn.setVisibility(View.GONE);
                       simpleExoPlayerView.setVisibility(View.GONE);
                   }
                } catch (JSONException e) {
                    FirebaseCrash.log(e.getMessage());
                } catch (IOException e) {
                    FirebaseCrash.log(e.getMessage());
                }finally{
                    response.body().close();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                FirebaseCrash.report(t);
            }
        });
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadImage().execute(URL);
            }
        });
    }
    private void preparingVideo(String url){
        if (player == null) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());

            simpleExoPlayerView.setPlayer(player);

            player.setPlayWhenReady(true);


            Uri uri = Uri.parse(url);
            MediaSource mediaSource = buildMediaSource(uri, bandwidthMeter);
            player.prepare(mediaSource, true, false);
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
        }
    }

    private MediaSource buildMediaSource(Uri uri,BandwidthMeter bandwidthMeter) {
        DataSource.Factory manifestDataSourceFactory = new DefaultHttpDataSourceFactory("ua");
        DashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(new DefaultHttpDataSourceFactory("ua"));
        return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(uri);
    }

    @Override
    protected void onPause() {
        if(player != null){
            player.stop();
            player.release();
        }
        super.onPause();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                supportFinishAfterTransition();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    class DownloadImage extends AsyncTask<String,Integer,Long> {
        ProgressDialog mProgressDialog = new ProgressDialog(DetailActivity.this);// Change Mainactivity.this with your activity name.
        String strFolderName;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage(getResources().getString(R.string.downloading));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.show();
        }
        @Override
        protected Long doInBackground(String... aurl) {
            int count;
            try {
                java.net.URL url = new URL((String) aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                String targetFileName="image"+".png";//Change name and subname
                int lenghtOfFile = conexion.getContentLength();
                String PATH = Environment.getExternalStorageDirectory()+ "/"+"Reddit"+"/";
                File folder = new File(PATH);
                if(!folder.exists()){
                    folder.mkdir();//If there is no folder it will be created.
                }
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(PATH+targetFileName);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress ((int)(total*100/lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {}
            return null;
        }
        protected void onProgressUpdate(Integer... progress) {
            mProgressDialog.setProgress(progress[0]);
            if(mProgressDialog.getProgress()==mProgressDialog.getMax()){
                mProgressDialog.dismiss();
                Toast.makeText(DetailActivity.this, getResources().getString(R.string.subreddit), Toast.LENGTH_SHORT).show();
            }
        }
        protected void onPostExecute(String result) {
        }
    }
}
