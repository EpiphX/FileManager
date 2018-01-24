package cj.com.fileexplorer.models;

/**
 * Credit model pojo that contains information (icon, name, description) about the entity being
 * credited.
 */
public class CreditModel {
    private String mName;
    private String mDescription;
    private int mDrawableResourceId;

    public CreditModel(String name, String description, int drawableResourceId) {
        mName = name;
        mDescription = description;
        mDrawableResourceId = drawableResourceId;
    }

    public String getName() {
        return mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getDrawableResourceId() {
        return mDrawableResourceId;
    }
}
