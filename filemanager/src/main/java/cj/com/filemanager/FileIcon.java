package cj.com.filemanager;

import java.util.HashMap;

/**
 * File icon provides mapping from the given file extension to the appropriate drawable id.
 */
public class FileIcon {
    private int mDrawableId = 0;
    private static HashMap<String, Integer> mExtensionTypeToDrawableMap = new HashMap<>();

    static {
        mExtensionTypeToDrawableMap.put("pdf", R.drawable.ic_pdf);
        mExtensionTypeToDrawableMap.put("jpeg", R.drawable.ic_jpg);
        mExtensionTypeToDrawableMap.put("jpg", R.drawable.ic_jpg);
        mExtensionTypeToDrawableMap.put("png", R.drawable.ic_png);
        mExtensionTypeToDrawableMap.put("mp3", R.drawable.ic_mp3);
        mExtensionTypeToDrawableMap.put("svg", R.drawable.ic_svg);
        mExtensionTypeToDrawableMap.put("txt", R.drawable.ic_txt);
    }

    public FileIcon(String fileExtension) {
        if (mExtensionTypeToDrawableMap.containsKey(fileExtension)) {
            mDrawableId = mExtensionTypeToDrawableMap.get(fileExtension);
        } else {
            mDrawableId = R.drawable.ic_txt;
        }
    }

    public int getDrawableId() {
        return mDrawableId;
    }
}
