package app.com.example.android.popularmovies.Database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ReviewListConverter {

    @TypeConverter
    public static List<MovieReview> fromString(String listString){
        if(listString == null){
            return null;
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type type = new TypeToken<ArrayList<MovieReview>>(){}.getType();
        return (List<MovieReview>) gson.fromJson(listString, type);
    }

    @TypeConverter
    public static String toString(List<MovieReview> list){
        if(list == null || list.size() == 0){
            return null;
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list);
    }
}
