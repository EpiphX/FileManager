package cj.com.fileexplorer.presenters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cj.com.filemanager.R;
import cj.com.fileexplorer.models.IconModel;
import cj.com.fileexplorer.views.CreditsView;

/**
 * Created by EpiphX on 1/13/18.
 */

public class CreditsPresenter {
    private WeakReference<CreditsView> mCreditsViewWeakReference;
    public CreditsPresenter(CreditsView creditsView) {
        mCreditsViewWeakReference = new WeakReference<CreditsView>(creditsView);
    }

    public void onCreditsRequested() {
        CreditsView creditsView = mCreditsViewWeakReference.get();

        if (creditsView != null) {
            ArrayList<IconModel> iconModels = new ArrayList<>();

            IconModel iconModel = new IconModel("pdf", R.drawable.ic_pdf);
            iconModels.add(iconModel);

            iconModel = new IconModel("svg", R.drawable.ic_svg);
            iconModels.add(iconModel);

            //TODO: Add more icon models for all used icons from Flat icon...

            creditsView.showCreditsForIcons(iconModels);
        }
    }
}
