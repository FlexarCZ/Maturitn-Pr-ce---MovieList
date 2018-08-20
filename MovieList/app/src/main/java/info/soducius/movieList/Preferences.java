package info.soducius.movieList;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class Preferences {
    public static final int NULL_INT = -1;
    public static final String NULL_STRING = null;
    private SharedPreferences mSharedPreferences;
    private Context mContext;
    private SharedPreferences.Editor editor;


    public Preferences(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
        editor = mSharedPreferences.edit();
    }

    public Boolean getFirstRun() {
        String key = "FirstRun";
        Boolean value = mSharedPreferences.getBoolean(key, true);
        return value;
    }

    public void setFirstRun(Boolean value) {
        String key = "FirstRun";
        editor.putBoolean(key, value);
        editor.commit();
    }

    public ArrayList<Integer> processToInteger(String[] strings) {
        ArrayList<Integer> integers = new ArrayList<>();
        for(String string:strings){
            integers.add(Integer.parseInt(string.trim()));
        }
        return integers;
    }

    public String[] processToString(ArrayList<Integer> integers) {
        String[] strings = new String[integers.size()];
        int i=0;
        for(Integer integer:integers){
            strings[i] = String.valueOf(integer);
            i++;
        }
        return strings;
    }

}
