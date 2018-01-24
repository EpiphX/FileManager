package cj.com.fileexplorer.presenters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cj.com.filemanager.R;
import cj.com.fileexplorer.models.CreditModel;
import cj.com.fileexplorer.views.CreditsView;

/**
 * Manages the generation and presentation of credit models.
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

            final String flatIconCreditText = "Icon made by [https://www.flaticon.com/authors/smashicons] from www.flaticon.com";

            CreditModel creditModel = new CreditModel("Pdf Icon", flatIconCreditText, R.drawable.ic_pdf);
            creditModels.add(creditModel);

            creditModel = new CreditModel("Svg Icon", flatIconCreditText, R.drawable.ic_svg);
            creditModels.add(creditModel);

            creditModel = new CreditModel("Jpg Icon", flatIconCreditText, R.drawable.ic_jpg);
            creditModels.add(creditModel);

            creditModel = new CreditModel("Png Icon", flatIconCreditText, R.drawable.ic_png);
            creditModels.add(creditModel);

            creditModel = new CreditModel("Txt Icon", flatIconCreditText, R.drawable.ic_txt);
            creditModels.add(creditModel);

            creditModel = new CreditModel("Mp3 Icon", flatIconCreditText, R.drawable.ic_mp3);
            creditModels.add(creditModel);

            creditModel = new CreditModel("Picasso", "Copyright 2013 Square, Inc.\n" +
                    "\n" +
                    "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                    "you may not use this file except in compliance with the License.\n" +
                    "You may obtain a copy of the License at\n" +
                    "\n" +
                    "   http://www.apache.org/licenses/LICENSE-2.0\n" +
                    "\n" +
                    "Unless required by applicable law or agreed to in writing, software\n" +
                    "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                    "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                    "See the License for the specific language governing permissions and\n" +
                    "limitations under the License.", -1);
            creditModels.add(creditModel);

            //TODO: Add more icon models for all used icons from Flat icon...

            creditsView.showCredits(creditModels);
        }
    }
}
