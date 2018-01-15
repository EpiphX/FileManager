package cj.com.fileexplorer.views;

import java.util.ArrayList;

import cj.com.fileexplorer.models.IconModel;

/**
 * View that shows credits to libraries and icons used by this application.
 */
public interface CreditsView {
    void showCreditsForIcons(ArrayList<IconModel> iconModels);
}
