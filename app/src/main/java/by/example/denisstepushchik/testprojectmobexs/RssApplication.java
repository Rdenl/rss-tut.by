package by.example.denisstepushchik.testprojectmobexs;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import by.example.denisstepushchik.testprojectmobexs.ormLite.DatabaseManager;

public class RssApplication extends Application {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseManager.getInstance().init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DatabaseManager.getInstance().release();
    }

    public void addTask(Runnable task) {
        executor.submit(task);
    }
}
