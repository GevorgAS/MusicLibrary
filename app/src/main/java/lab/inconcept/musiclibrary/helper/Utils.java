package lab.inconcept.musiclibrary.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lab.inconcept.musiclibrary.model.MusicModel;

public final class Utils {
    private static final String AUTHOR = "author";
    private static final String ALBUM = "album";
    private static final String SONG_NAME = "name";
    private static final String IMAGE_URL = "imageUrl";

    public static void hideKeyboard(View view) {
        if (view != null) {
            final InputMethodManager inputMethodManager = (InputMethodManager) view.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputMethodManager == null) {
                return;
            }
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static ArrayList<MusicModel> convertToModels(HashMap<String, ArrayList> hashMaps) {
        final ArrayList<MusicModel> songs = new ArrayList<>();
        for (Map.Entry<String, ArrayList> stringArrayListEntry : hashMaps.entrySet()) {
            final MusicModel model = new MusicModel();
            final HashMap map = (HashMap) ((HashMap.Entry) stringArrayListEntry).getValue();
            model.setName((String) map.get(SONG_NAME));
            model.setAlbum((String) map.get(ALBUM));
            model.setImageUrl((String) map.get(IMAGE_URL));
            model.setAuthor((String) map.get(AUTHOR));
            songs.add(model);
        }
        return songs;
    }

    public static MusicModel getModel(HashMap hashMap) {
        final MusicModel model = new MusicModel();
        model.setName((String) hashMap.get(SONG_NAME));
        model.setAlbum((String) hashMap.get(ALBUM));
        model.setImageUrl((String) hashMap.get(IMAGE_URL));
        model.setAuthor((String) hashMap.get(AUTHOR));
        return model;
    }

    static byte[] bitmapToByteArray(Bitmap bitmap) {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static Bitmap byteArrayToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
