package xyz.ashioto.ashioto;

import android.app.Application;
import android.support.annotation.NonNull;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by geek on 18/11/16.
 * Application class for singletons
 */

public class ApplicationClass extends Application {
    private static ApplicationClass instance;
    private static Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static synchronized ApplicationClass getInstance(){
        return instance;
    }

    //Retrofit singleton
    @NonNull
    public static Retrofit getRetrofit(){
         retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.base_url)
                .addConverterFactory(GsonConverterFactory.create()).build();

        return retrofit;
    }

    @NonNull
    public static AshiotoRetrofitInterface getRetrofitInterface(){
        return getRetrofit().create(AshiotoRetrofitInterface.class);
    }
}
