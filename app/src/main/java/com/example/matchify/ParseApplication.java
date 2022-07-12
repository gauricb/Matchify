package com.example.matchify;

import android.app.Application;

import com.example.matchify.models.Message;
import com.example.matchify.models.Song;
import com.example.matchify.models.SpotifyUser;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(SpotifyUser.class);
        ParseObject.registerSubclass(Song.class);
        //ParseObject.registerSubclass(ParseObject.class);
        // Use for monitoring Parse network traffic
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        // any network interceptors must be added with the Configuration Builder given this syntax
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // Set applicationId and server based on the values in the Back4App settings.
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("rajIk3NX2rXDRLbteb1QstpdtXWjCg17l0LZ517E")
                .clientKey("CYSG4ZOAHxB9Xe9DSOxhQIQdNJIbORdfJYsCmH8h")
                .clientBuilder(builder)
                .server("https://parseapi.back4app.com").build());  // ⚠️ TYPE IN A VALID SERVER URL HERE
    }
}
