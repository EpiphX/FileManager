package cj.com.filemanager;

import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Utility functions for interacting with {@link File}
 *
 * As of date, includes the following methods:
 *
 * {@link #getFileExtension(File)} to get the file extension of a given file.
 *
 * {@link #getMimeType(String)} to get the mime type given a string url.
 */
public class FileUtils {
    /**
     * Gets the file extension for a given file.
     *
     * @param file
     *
     * @return      Returns the file extension if found.
     *              Returns EMPTY if an exception is thrown.
     */
    public static String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Gets the mime type of a given string url.
     *
     * @param url
     *
     * @return      The Mime type of the url.
     *              @see MimeTypeMap
     */
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
