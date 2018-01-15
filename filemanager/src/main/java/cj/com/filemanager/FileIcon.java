package cj.com.filemanager;

import java.util.HashMap;

/**
 * Created by EpiphX on 1/14/18.
 */

public class FileIcon {
    private int mDrawableId;

    public FileIcon(String fileExtension) {
        HashMap<String, Integer> extensionTypeToDrawableMap = new HashMap<>();
        extensionTypeToDrawableMap.put("pdf", R.drawable.ic_pdf);
        extensionTypeToDrawableMap.put("jpeg", R.drawable.ic_jpg);
        extensionTypeToDrawableMap.put("jpg", R.drawable.ic_jpg);
        extensionTypeToDrawableMap.put("png", R.drawable.ic_png);
        extensionTypeToDrawableMap.put("mp3", R.drawable.ic_mp3);
        extensionTypeToDrawableMap.put("svg", R.drawable.ic_svg);
        extensionTypeToDrawableMap.put("txt", R.drawable.ic_txt);

        if (extensionTypeToDrawableMap.containsKey(fileExtension)) {
            mDrawableId = extensionTypeToDrawableMap.get(fileExtension);
        } else {
            mDrawableId = R.drawable.ic_txt;
        }
    }

    public int getDrawableId() {
        return mDrawableId;
    }
}
