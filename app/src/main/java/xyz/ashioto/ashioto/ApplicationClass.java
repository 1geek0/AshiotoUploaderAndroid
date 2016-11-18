package xyz.ashioto.ashioto;

import android.app.Application;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by geek on 18/11/16.
 * Application class for singletons
 */

public class ApplicationClass extends Application {
    private static ApplicationClass instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static synchronized ApplicationClass getInstance(){
        return instance;
    }

    //Retrofit singleton
    public static Retrofit getRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.base_url)
                .addConverterFactory(GsonConverterFactory.create()).build();
    }
}
