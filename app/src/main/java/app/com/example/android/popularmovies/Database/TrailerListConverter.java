package app.com.example.android.popularmovies.Database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TrailerListConverter {

    @TypeConverter
    public static List<MovieTrailer> fromString(String listString){
        if(listString == null){
            return null;
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type type = new TypeToken<ArrayList<MovieTrailer>>(){}.getType();
        return (List<MovieTrailer>) gson.fromJson(listString, type);
    }

    @TypeConverter
    public static String toString(List<MovieTrailer> list){
        if(list == null || list.size() == 0){
            return null;
        }

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list);
    }
}
