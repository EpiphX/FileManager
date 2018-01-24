package cj.com.fileexplorer.models;

public class CreditModel {
    private String mName;
    private int mDrawableResourceId;

    public CreditModel(String name, int drawableResourceId) {
        mName = name;
        mDrawableResourceId = drawableResourceId;
    }

    public String getName() {
        return mName;
    }

    public int getDrawableResourceId() {
        return mDrawableResourceId;
    }
}
