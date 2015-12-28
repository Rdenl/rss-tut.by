package by.example.denisstepushchik.testprojectmobexs.service;

import by.example.denisstepushchik.testprojectmobexs.data.Rss;
import retrofit.Call;
import retrofit.http.GET;

public interface Service {

    @GET("/rss/all.rss")
    Call<Rss> getRss();
}