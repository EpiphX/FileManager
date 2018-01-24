package cj.com.fileexplorer.presenters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cj.com.filemanager.R;
import cj.com.fileexplorer.models.CreditModel;
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
            ArrayList<CreditModel> creditModels = new ArrayList<>();

            CreditModel creditModel = new CreditModel("pdf", R.drawable.ic_pdf);
            creditModels.add(creditModel);

            creditModel = new CreditModel("svg", R.drawable.ic_svg);
            creditModels.add(creditModel);

            //TODO: Add more icon models for all used icons from Flat icon...

            creditsView.showCreditsForIcons(creditModels);
        }
    }
}
