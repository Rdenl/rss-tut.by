package by.example.denisstepushchik.testprojectmobexs.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import by.example.denisstepushchik.testprojectmobexs.R;
import by.example.denisstepushchik.testprojectmobexs.data.ArticleDB;
import by.example.denisstepushchik.testprojectmobexs.ormLite.DatabaseManager;
import by.example.denisstepushchik.testprojectmobexs.service.PicassoBigCache;
import by.example.denisstepushchik.testprojectmobexs.views.MarqueeToolbar;


public class ArticleFragment extends Fragment {

    private static final String ARTICLE_POSITION = "position";
    private AppCompatActivity mAppCompatActivity;
    private ArticleDB mArticle;

    public ArticleFragment() {
    }

    public static ArticleFragment newInstance(int position) {
        ArticleFragment fragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putInt(ARTICLE_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mAppCompatActivity = (AppCompatActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must extends AppCompatActivity");
        }
    }

    /*
     * Deprecated on API 23
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                this.mAppCompatActivity = (AppCompatActivity) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must extends AppCompatActivity");
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int position = 0;
        if (getArguments() != null) {
            position = getArguments().getInt(ARTICLE_POSITION);
        }

        try {
            Dao<ArticleDB, Integer> articleDao = DatabaseManager.getInstance().getHelper().getArticleDAO();
            int numberArticles = articleDao.queryForAll().size();
            mArticle = articleDao.queryForAll().get(numberArticles - 1 - position);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_article, container, false);

        ImageView articleImage = (ImageView) view.findViewById(R.id.articleImage);
        TextView articlePubDate = (TextView) view.findViewById(R.id.articlePubDate);
        TextView articleText = (TextView) view.findViewById(R.id.articleText);
        TextView articleLink = (TextView) view.findViewById(R.id.articleLink);

        PicassoBigCache.INSTANCE.getPicassoBigCache(mAppCompatActivity)
                .load(mArticle.getImageUrl())
                .error(R.drawable.no_image)
                .into(articleImage);

        articlePubDate.setText(mArticle.getPubDate());
        articleText.setText(mArticle.getText());
        articleLink.setText(mArticle.getLink());

        MarqueeToolbar toolbar = (MarqueeToolbar) view.findViewById(R.id.toolbar);
        mAppCompatActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = mAppCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        toolbar.setTitle(mArticle.getTitle());

        return view;
    }
}
