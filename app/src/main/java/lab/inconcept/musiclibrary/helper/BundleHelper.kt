package lab.inconcept.musiclibrary.helper

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import lab.inconcept.musiclibrary.model.MusicModel

object BundleHelper {
    private const val BUNDLE_MUSIC_MODEL = "bundle_music_model"
    fun bundleMusicModel(fragment: Fragment, musicModel: MusicModel?) {
        val gson = Gson()
        val json = gson.toJson(musicModel)
        val bundle = Bundle()
        bundle.putString(BUNDLE_MUSIC_MODEL, json)
        fragment.arguments = bundle
    }

    fun getMusicModel(fragment: Fragment): MusicModel? {
        if (fragment.arguments == null) {
            return null
        }
        val json = fragment.arguments!!.getString(BUNDLE_MUSIC_MODEL)
        if (json == null || json.isEmpty()) {
            return null
        }
        val gson = Gson()
        return gson.fromJson(json, MusicModel::class.java)
    }
}