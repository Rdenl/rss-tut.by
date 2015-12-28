package by.example.denisstepushchik.testprojectmobexs;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.j256.ormlite.dao.Dao;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import by.example.denisstepushchik.testprojectmobexs.utils.Constants;
import by.example.denisstepushchik.testprojectmobexs.utils.Utils;
import by.example.denisstepushchik.testprojectmobexs.data.Article;
import by.example.denisstepushchik.testprojectmobexs.data.ArticleDB;
import by.example.denisstepushchik.testprojectmobexs.data.Enclosure;
import by.example.denisstepushchik.testprojectmobexs.data.Rss;
import by.example.denisstepushchik.testprojectmobexs.ormLite.DatabaseManager;
import by.example.denisstepushchik.testprojectmobexs.service.Service;
import retrofit.Call;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.SimpleXmlConverterFactory;

public class UpdateDateTask implements Runnable {

    private OnUpdateListener mListener;

    private Context mContext;

    public static final int CODE_SUCCESS = 1;
    public static final int CODE_NETWORK_ERROR = 2;
    public static final int CODE_COMMON_ERROR = 3;

    private boolean mIsArticleAdded = false;

    public interface OnUpdateListener {
        void onUpdateFinished(int resultCode, boolean isArticleAdded);
    }

    public UpdateDateTask(OnUpdateListener listener, Context context) {
        this.mListener = listener;
        this.mContext = context;
    }

    @Override
    public void run() {
        if (!Utils.isOnline(mContext)) {
            setInitFinished(CODE_NETWORK_ERROR);

        } else {
            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(15, TimeUnit.SECONDS);
            client.setReadTimeout(15, TimeUnit.SECONDS);
            client.setWriteTimeout(15, TimeUnit.SECONDS);

            Retrofit restAdapter = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .build();

            Service service = restAdapter.create(Service.class);

            Call<Rss> call = service.getRss();
            try {
                Response<Rss> rss = call.execute();

                Dao<ArticleDB, Integer> ArticleDao = DatabaseManager.getInstance().getHelper().getArticleDAO();

                int articleNumber = ArticleDao.queryForAll().size();

                if (articleNumber != 0) {
                    List<Article> allArticles = rss.body().getChannel().getArticles();

                    List<Article> articles = new ArrayList<>();
                    List<ArticleDB> articlesDB = ArticleDao.queryForAll();

                    for (Article article : allArticles) {
                        boolean isArticlesEqual = false;
                        boolean isAdded = false;

                        for (int i = articlesDB.size() - 1; i >= 0; i--) {
                            ArticleDB articleDB = articlesDB.get(i);
                            if (article.getTitle().equals(articleDB.getTitle())) {
                                isArticlesEqual = true;
                                break;
                            }
                        }

                        if(!isArticlesEqual){
                            articles.add(article);
                            isAdded = true;
                        }

                        if (!isAdded) {
                            break;
                        }
                    }

                    for (int i = articles.size() - 1; i >= 0; i--) {

                        Article article = articles.get(i);

                        String text = getText(article);
                        String iconURL = getIconURL(article);
                        String imageUrl = getImageURL(article);

                        String pubDate = parseTutByDate(article.getPubDate());
                        ArticleDao.createIfNotExists(new ArticleDB(article.getTitle(), pubDate, imageUrl, text, iconURL, article.getLink()));
                    }
                    if (articleNumber != ArticleDao.queryForAll().size()) {
                        mIsArticleAdded = true;
                    }
                } else {

                    mIsArticleAdded = true;

                    List<Article> articles = rss.body().getChannel().getArticles();
                    for (int i = articles.size() - 1; i >= 0; i--) {

                        Article article = articles.get(i);

                        String text = getText(article);
                        String iconURL = getIconURL(article);
                        String imageUrl = getImageURL(article);

                        String pubDate = parseTutByDate(article.getPubDate());
                        ArticleDao.create(new ArticleDB(article.getTitle(), pubDate, imageUrl, text, iconURL, article.getLink()));
                    }
                }

                deleteRows(ArticleDao);

                setInitFinished(CODE_SUCCESS);

            } catch (SQLException | IOException e) {
                e.printStackTrace();
                setInitFinished(CODE_COMMON_ERROR);
            }
        }
    }

    private String getText(Article article) {
        String text = null;
        if (article.getDescription() != null) {

            String[] descriptionText = article.getDescription().split(">");
            if (descriptionText.length > 1) {
                text = descriptionText[1].split("<")[0];
            } else {
                text = article.getDescription();
            }
        }
        return text;

    }

    private String getIconURL(Article article) {
        String iconURL = null;
        if (article.getDescription() != null) {

            String[] descriptionIconURL = article.getDescription().split("\"");
            if (descriptionIconURL.length > 1) {
                iconURL = descriptionIconURL[1];
            } else {
                iconURL = null;
            }
        }
        return iconURL;
    }

    private String getImageURL(Article article) {
        String imageUrl = null;

        Enclosure enclosure = article.getEnclosure();
        if (enclosure != null) {
            imageUrl = enclosure.getUrl();
        }
        return imageUrl;
    }


    private void setInitFinished(final int resultCode) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                if (mListener != null) {
                    mListener.onUpdateFinished(resultCode, mIsArticleAdded);
                }
            }
        });
    }

    private void deleteRows(Dao<ArticleDB, Integer> ArticleDao) throws SQLException {
        while (ArticleDao.countOf() > Constants.MAX_COUNT_OF_ARTICLE) {
            ArticleDao.delete(ArticleDao.queryForAll().get(0));
        }
    }

    public String parseTutByDate(String time) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(Constants.TUT_BY_DATE_FORMAT, Constants.DATE_LOCALE);
        SimpleDateFormat outputFormat = new SimpleDateFormat(Constants.APP_DATE_FORMAT, Locale.getDefault());

        String outputDate = null;
        try {
            Date date = inputFormat.parse(time);
            outputDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDate;
    }
}
