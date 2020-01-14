package com.example.martinosama.capstone2;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class SubReddits {

    @SerializedName("data")
    @Expose
    private Data data;

    /**
     * No args constructor for use in serialization
     *
     */
    public SubReddits() {
    }

    /**
     *
     * @param data
     */
    public SubReddits(Data data) {
        super();
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
    class Child {

        @SerializedName("data")
        @Expose
        private Data_ data;

        /**
         * No args constructor for use in serialization
         *
         */
        public Child() {
        }

        /**
         *
         * @param data
         */
        public Child(Data_ data) {
            super();
            this.data = data;
        }

        public Data_ getData() {
            return data;
        }

        public void setData(Data_ data) {
            this.data = data;
        }

    }
    class Data {

        @SerializedName("dist")
        @Expose
        private Integer dist;
        @SerializedName("children")
        @Expose
        private List<Child> children = null;

        /**
         * No args constructor for use in serialization
         *
         */
        public Data() {
        }

        /**
         *
         * @param children
         * @param dist
         */
        public Data(Integer dist, List<Child> children) {
            super();
            this.dist = dist;
            this.children = children;
        }

        public Integer getDist() {
            return dist;
        }

        public void setDist(Integer dist) {
            this.dist = dist;
        }

        public List<Child> getChildren() {
            return children;
        }

        public void setChildren(List<Child> children) {
            this.children = children;
        }

    }
    class Data_ {

        @SerializedName("display_name")
        @Expose
        private String displayName;
        @SerializedName("header_img")
        @Expose
        private String headerImg;
        @SerializedName("over18")
        @Expose
        private Boolean over18;

        /**
         * No args constructor for use in serialization
         *
         */
        public Data_() {
        }

        /**
         *
         * @param headerImg
         * @param over18
         * @param displayName
         */
        public Data_(String displayName, String headerImg, Boolean over18) {
            super();
            this.displayName = displayName;
            this.headerImg = headerImg;
            this.over18 = over18;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getHeaderImg() {
            return headerImg;
        }

        public void setHeaderImg(String headerImg) {
            this.headerImg = headerImg;
        }

        public Boolean getOver18() {
            return over18;
        }

        public void setOver18(Boolean over18) {
            this.over18 = over18;
        }

    }

}