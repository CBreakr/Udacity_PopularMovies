package app.com.example.android.popularmovies.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import app.com.example.android.popularmovies.R;

public class FilterUtils {
    public enum FilterType {
        Popular,
        TopRated,
        Favorite
    }

    //
    // SHARED PREFERENCES
    //
    public static void setFilterType(FilterType ft, Context context){
        if(ft == null){
            ft = getFilterTypeFromSharedPreferences(context);
        }

        writeFilterTypeToSharedPreferences(ft, context);
    }

    public static FilterType getFilterTypeFromSharedPreferences(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String filter =
                pref.getString(
                        context.getString(R.string.Key_FilterTypeSharedPreferences)
                        , null);
        return getFilterTypeFromString(filter, context);
    }

    public static boolean isFavoriteMode(Context context){
        String filter = getFilterTypeSharedPreferencesString(context);

        if(filter == getStringFromFilterType(FilterType.Favorite, context)){
            return true;
        }

        return false;
    }

    //
    // public wrapper to have a better name
    //
    public static String getCurrentFilterTypeAsString(Context context){
        return getFilterTypeSharedPreferencesString(context);
    }

    private static String getFilterTypeSharedPreferencesString(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String filter = pref.getString(
                context.getString(R.string.Key_FilterTypeSharedPreferences)
                , null);
        return filter;
    }

    private static void writeFilterTypeToSharedPreferences(FilterType ft, Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(
                context.getString(R.string.Key_FilterTypeSharedPreferences)
                , getStringFromFilterType(ft, context));
        editor.commit();
    }

    //
    // because I can't use an enum within SharedPreferences....
    //
    private static String getStringFromFilterType(FilterType ft, Context context){
        switch(ft){
            case Popular:
                return context.getString(R.string.FilterTypeForSharedPreferences_Popular);
            case TopRated:
                return context.getString(R.string.FilterTypeForSharedPreferences_TopRated);
            case Favorite:
                return context.getString(R.string.FilterTypeForSharedPreferences_Favorite);
        }

        return null;
    }

    private static FilterType getFilterTypeFromString(String s, Context context){
        if (s.equalsIgnoreCase(context.getString(R.string.FilterTypeForSharedPreferences_TopRated))){
            return FilterType.TopRated;
        }
        else if(s.equalsIgnoreCase(context.getString(R.string.FilterTypeForSharedPreferences_Favorite))){
            return FilterType.Favorite;
        }

        // this will always be the default
        return FilterType.Popular;
    }
}
