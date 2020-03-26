package auto.app.model;

import com.google.firebase.database.Exclude;

public class Advertisement {
    private String mTitle;
    private String mPrice;
    private String mDescription;
    private String mImageUrl;
    private Boolean isFav;
    private String key;

    public Advertisement() {
    }

    public Advertisement(String mTitle, String mPrice, String mDescription, String mImageUrl) {
        this.mTitle = mTitle;
        this.mPrice = mPrice;
        this.mDescription = mDescription;
        this.mImageUrl = mImageUrl;
        this.isFav = false;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmPrice() {
        return mPrice;
    }

    public void setmPrice(String mPrice) {
        this.mPrice = mPrice;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public Boolean getFav() {
        return isFav;
    }

    public void setFav(Boolean fav) {
        isFav = fav;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
