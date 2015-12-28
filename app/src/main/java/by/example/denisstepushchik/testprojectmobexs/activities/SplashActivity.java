package by.example.denisstepushchik.testprojectmobexs.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import by.example.denisstepushchik.testprojectmobexs.R;
import by.example.denisstepushchik.testprojectmobexs.RssApplication;
import by.example.denisstepushchik.testprojectmobexs.UpdateDateTask;

public class SplashActivity extends Activity implements UpdateDateTask.OnUpdateListener {

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);

        ((RssApplication) getApplication()).addTask(new UpdateDateTask(this, this));
    }

    private void startMainActivity() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }

    @Override
    public void onUpdateFinished(int resultCode, boolean isArticleAdded) {

        switch (resultCode) {
            case 2:
                String network_error = getString(R.string.network_error);
                Toast.makeText(getBaseContext(), network_error, Toast.LENGTH_SHORT).show();
            case 1:
            case 3:
            default:
                startMainActivity();
                break;

        }
    }
}
