package lab.inconcept.musiclibrary.helper

import android.content.Context

object PreferenceManager {
    private const val PREFERENCES_TAG = "preferences"

    fun putString(context: Context, key: String?, value: String?) {
        val sPref = context.getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE)
        val ed = sPref.edit()
        ed.putString(key, value)
        ed.apply()
    }

    fun getString(context: Context, key: String?, defValue: String?): String? {
        val sPref = context.getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE)
        return sPref.getString(key, defValue)
    }
}