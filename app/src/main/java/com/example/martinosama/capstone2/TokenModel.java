package com.example.martinosama.capstone2;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenModel implements Parcelable
{

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("token_type")
    @Expose
    private String tokenType;
    @SerializedName("expires_in")
    @Expose
    private Float expiresIn;
    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;
    @SerializedName("scope")
    @Expose
    private String scope;
    public final static Parcelable.Creator<TokenModel> CREATOR = new Creator<TokenModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TokenModel createFromParcel(Parcel in) {
            return new TokenModel(in);
        }

        public TokenModel[] newArray(int size) {
            return (new TokenModel[size]);
        }

    }
            ;

    protected TokenModel(Parcel in) {
        this.accessToken = ((String) in.readValue((String.class.getClassLoader())));
        this.tokenType = ((String) in.readValue((String.class.getClassLoader())));
        this.expiresIn = ((Float) in.readValue((Float.class.getClassLoader())));
        this.refreshToken = ((String) in.readValue((String.class.getClassLoader())));
        this.scope = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public TokenModel() {
    }

    /**
     *
     * @param scope
     * @param tokenType
     * @param accessToken
     * @param expiresIn
     * @param refreshToken
     */
    public TokenModel(String accessToken, String tokenType, Float expiresIn, String refreshToken, String scope) {
        super();
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.scope = scope;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Float getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Float expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(accessToken);
        dest.writeValue(tokenType);
        dest.writeValue(expiresIn);
        dest.writeValue(refreshToken);
        dest.writeValue(scope);
    }

    public int describeContents() {
        return 0;
    }

}