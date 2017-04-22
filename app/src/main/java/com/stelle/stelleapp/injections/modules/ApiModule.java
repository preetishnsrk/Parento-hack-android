package com.stelle.stelleapp.injections.modules;


import com.stelle.stelleapp.BuildSchemeConstants;
import com.stelle.stelleapp.getstarted.interfaces.SplashWebServiceInterface;
import com.stelle.stelleapp.homescreen.interfaces.HomeScreenWebServiceInterface;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;

@Module
public class ApiModule {

    @Provides
    @Singleton
    public HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    @Provides
    @Singleton
    public OkHttpClient provideClient(HttpLoggingInterceptor loggingInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .build();

                return chain.proceed(request);
            }
        });
        builder.connectTimeout(120, TimeUnit.SECONDS);
        return builder.build();
    }


    @Provides
    @Singleton
    public Retrofit provideRetrofitBuilder(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BuildSchemeConstants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public SplashWebServiceInterface provideService(Retrofit retrofit) {
        return retrofit.create(SplashWebServiceInterface.class);
    }

    @Provides
    @Singleton
    public HomeScreenWebServiceInterface provideHomeScreenService(Retrofit retrofit) {
        return retrofit.create(HomeScreenWebServiceInterface.class);
    }

}
