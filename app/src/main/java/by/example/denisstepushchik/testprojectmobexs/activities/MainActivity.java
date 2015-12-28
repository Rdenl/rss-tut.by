package by.example.denisstepushchik.testprojectmobexs.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import by.example.denisstepushchik.testprojectmobexs.R;
import by.example.denisstepushchik.testprojectmobexs.fragments.ArticlePreviewFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }
            getFragmentManager().beginTransaction().add(R.id.fragment_container, new ArticlePreviewFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
