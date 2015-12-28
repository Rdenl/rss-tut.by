package by.example.denisstepushchik.testprojectmobexs.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import by.example.denisstepushchik.testprojectmobexs.DividerItemDecoration;
import by.example.denisstepushchik.testprojectmobexs.HidingScrollListener;
import by.example.denisstepushchik.testprojectmobexs.R;
import by.example.denisstepushchik.testprojectmobexs.RssApplication;
import by.example.denisstepushchik.testprojectmobexs.UpdateDateTask;
import by.example.denisstepushchik.testprojectmobexs.adapters.ArticlePreviewAdapter;
import by.example.denisstepushchik.testprojectmobexs.data.ArticleDB;
import by.example.denisstepushchik.testprojectmobexs.ormLite.DatabaseManager;

public class ArticlePreviewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context mContext;

    private List<ArticleDB> mArticles;
    private int mArticleNumber;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ArticlePreviewAdapter mArticlePreviewAdapter;
    private TextView mEmptyView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public ArticlePreviewFragment() {
    }

    private final ArticlePreviewAdapter.OnItemClickListener mOnItemClickListener = new ArticlePreviewAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View view, int position) {
            ArticleFragment articleFragment = ArticleFragment.newInstance(position); //(mArticleNumber - 1 - position);

            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, articleFragment)
                    .addToBackStack(null)
                    .commit();
        }
    };


    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onAttachToContext(context);
        this.mContext = context;
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            onAttachToContext(activity);
        }
    }

    /*
     * Called when the fragment attaches to the context
     */
    protected void onAttachToContext(Context context) {
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setArticles();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_preview, container, false);

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.toolbar_text));

        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mArticlePreviewAdapter = new ArticlePreviewAdapter(mContext, mArticles, mOnItemClickListener);
        mRecyclerView.setAdapter(mArticlePreviewAdapter);

        mEmptyView = (TextView) view.findViewById(R.id.mEmptyView);

        mRecyclerView.addOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2));
            }

            @Override
            public void onShow() {
                mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.mSwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        return view;
    }

    private void checkAdapterIsEmpty() {
        if (mArticlePreviewAdapter.getItemCount() > 0) {
            mEmptyView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAdapterIsEmpty();
    }

    @Override
    public void onPause() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }
        super.onPause();
    }

    @Override
    public void onRefresh() {

        ((RssApplication) getActivity().getApplication()).addTask(new UpdateDateTask(new UpdateDateTask.OnUpdateListener() {

            @Override
            public void onUpdateFinished(int resultCode, boolean isArticleAdded) {

                switch (resultCode) {
                    case 1:
                        if (isArticleAdded) {

                            setArticles();
                            mArticlePreviewAdapter = new ArticlePreviewAdapter(mContext, mArticles, mOnItemClickListener);
                            mRecyclerView.setAdapter(mArticlePreviewAdapter);

                            checkAdapterIsEmpty();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                    case 2:
                        String network_error = getString(R.string.network_error);
                        Toast.makeText(mContext, network_error, Toast.LENGTH_LONG).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                    case 3:
                    default:
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                }
            }
        }, mContext));
    }

    private void setArticles() {
        mArticles = new ArrayList<>();
        try {
            Dao<ArticleDB, Integer> articleDao = DatabaseManager.getInstance().getHelper().getArticleDAO();
            mArticles = articleDao.queryForAll();
            mArticleNumber = mArticles.size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
