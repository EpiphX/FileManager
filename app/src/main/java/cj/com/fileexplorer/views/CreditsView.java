package cj.com.fileexplorer.views;

import java.util.ArrayList;

import cj.com.fileexplorer.models.CreditModel;

/**
 * View that shows credits to libraries and icons used by this application.
 */
public interface CreditsView {
    /**
     * Shows the given credit models to the user.
     *
     * @param creditModels
     */
    void showCredits(ArrayList<CreditModel> creditModels);
}
