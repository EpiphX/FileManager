package cj.com.fileexplorer.models;

public class IconModel {
    private String mName;
    private int mDrawableResourceId;

    public IconModel(String name, int drawableResourceId) {
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
