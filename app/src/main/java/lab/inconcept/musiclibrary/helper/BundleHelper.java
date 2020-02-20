package lab.inconcept.musiclibrary.helper;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import lab.inconcept.musiclibrary.model.MusicModel;

public class BundleHelper {
    private static final String BUNDLE_MUSIC_MODEL = "bundle_music_model";

    public static void bundleMusicModel(Fragment fragment, MusicModel musicModel) {
        final Gson gson = new Gson();
        final String json = gson.toJson(musicModel);
        final Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_MUSIC_MODEL, json);
        fragment.setArguments(bundle);
    }

    public static MusicModel getMusicModel(final Fragment fragment) {
        if (fragment.getArguments() == null) {
            return null;
        }
        final String json = fragment.getArguments().getString(BUNDLE_MUSIC_MODEL);
        if (json == null || json.isEmpty()) {
            return null;
        }
        final Gson gson = new Gson();
        return gson.fromJson(json, MusicModel.class);
    }
}
