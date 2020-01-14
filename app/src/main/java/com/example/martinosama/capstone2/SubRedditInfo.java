package com.example.martinosama.capstone2;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubRedditInfo implements Parcelable {

    @SerializedName("subreddit")
    @Expose
    private String subreddit;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("over_18")
    @Expose
    private Boolean over18;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("created_utc")
    @Expose
    private Double createdUtc;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("permalink")
    @Expose
    private String permalink;

    @SerializedName("after")
    @Expose
    private String after;

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }


    /**
     * No args constructor for use in serialization
     *
     */
    public SubRedditInfo() {
    }

    /**
     *
     * @param createdUtc
     * @param author
     * @param title
     * @param thumbnail
     * @param permalink
     * @param over18
     * @param subreddit
     */
    public SubRedditInfo(String subreddit, String title, Boolean over18, String author, Double createdUtc, String thumbnail, String permalink,String after) {
        super();
        this.subreddit = subreddit;
        this.title = title;
        this.over18 = over18;
        this.author = author;
        this.createdUtc = createdUtc;
        this.thumbnail = thumbnail;
        this.permalink = permalink;

        this.after = after;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getOver18() {
        return over18;
    }

    public void setOver18(Boolean over18) {
        this.over18 = over18;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Double getCreatedUtc() {
        return createdUtc;
    }

    public void setCreatedUtc(Double createdUtc) {
        this.createdUtc = createdUtc;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }



    protected SubRedditInfo(Parcel in) {
        subreddit = in.readString();
        title = in.readString();
        byte over18Val = in.readByte();
        over18 = over18Val == 0x02 ? null : over18Val != 0x00;
        author = in.readString();
        createdUtc = in.readByte() == 0x00 ? null : in.readDouble();
        thumbnail = in.readString();
        permalink = in.readString();
        after = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subreddit);
        dest.writeString(title);
        if (over18 == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (over18 ? 0x01 : 0x00));
        }
        dest.writeString(author);
        if (createdUtc == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(createdUtc);
        }
        dest.writeString(thumbnail);
        dest.writeString(permalink);
        dest.writeString(after);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SubRedditInfo> CREATOR = new Parcelable.Creator<SubRedditInfo>() {
        @Override
        public SubRedditInfo createFromParcel(Parcel in) {
            return new SubRedditInfo(in);
        }

        @Override
        public SubRedditInfo[] newArray(int size) {
            return new SubRedditInfo[size];
        }
    };
}