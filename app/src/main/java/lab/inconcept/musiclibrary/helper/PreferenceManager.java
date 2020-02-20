package lab.inconcept.musiclibrary.helper;

import android.content.Context;
import android.content.SharedPreferences;

public final class PreferenceManager {
    private static final String PREFERENCES_TAG = "preferences";

    public static void putString(Context context, String key, String value) {
        final SharedPreferences sPref = context.getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE);
        final SharedPreferences.Editor ed = sPref.edit();
        ed.putString(key, value);
        ed.apply();
    }

    public static String getString(Context context, String key, String defValue) {
        final SharedPreferences sPref = context.getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE);
        return sPref.getString(key, defValue);
    }

}
