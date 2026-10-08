package pt.isep.dssmv.mrgym.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsUtils {
    private static final String PREF_NAME = "MRGymPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TOKEN = "token";

    public static void saveSession(Context context, String email, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public static String getEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_EMAIL, null);
    }
    
    public static void clearSession(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
