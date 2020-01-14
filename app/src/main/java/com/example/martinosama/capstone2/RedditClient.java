package com.example.martinosama.capstone2;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RedditClient {

    @POST("/api/v1/access_token")
    Call<TokenModel> getToken(@Header("User-Agent") String userAgent, @Query("state") String state, @Query("scope") String scope, @Query("client_id") String client
    , @Query("redirect_uri") String redirect, @Query("code") String code, @Query("grant_type") String grant);

    @GET("/subreddits/mine/subscriber")
    Call<SubReddits> getSubReddits(@Query("limit") int limit);
    @GET("/r/{subreddit}/new.json")
    Call<ResponseBody> getSubRedditInfo(@Path("subreddit") String subreddit, @Query("after") String after, @Query("limit") int limit);
    @GET
    Call<ResponseBody> getPostInfo(@Url String path);
}
