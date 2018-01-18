package cj.com.fileexplorer.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import cj.com.fileexplorer.R;
import cj.com.fileexplorer.adapters.CreditsAdapter;
import cj.com.fileexplorer.models.IconModel;
import cj.com.fileexplorer.presenters.CreditsPresenter;

public class CreditsActivity extends AppCompatActivity implements CreditsView {

    private CreditsAdapter mCreditsAdapter;

    private CreditsPresenter mCreditsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCreditsPresenter = new CreditsPresenter(this);

        RecyclerView creditsRecyclerView = findViewById(R.id.creditsRecyclerView);
        creditsRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext(),
                LinearLayoutManager.VERTICAL, false));

        mCreditsAdapter = new CreditsAdapter();
        creditsRecyclerView.setAdapter(mCreditsAdapter);

        mCreditsPresenter.onCreditsRequested();
    }

    @Override
    public void showCreditsForIcons(ArrayList<IconModel> iconModels) {
        mCreditsAdapter.showIcons(iconModels);
    }
}
