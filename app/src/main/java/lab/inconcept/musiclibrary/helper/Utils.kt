package lab.inconcept.musiclibrary.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.view.inputmethod.InputMethodManager
import lab.inconcept.musiclibrary.model.MusicModel
import java.util.*

object Utils {
    private const val AUTHOR = "author"
    private const val ALBUM = "album"
    private const val SONG_NAME = "name"
    private const val IMAGE_URL = "imageUrl"

    fun hideKeyboard(view: View?) {
        if (view != null) {
            val inputMethodManager = view.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun getModel(hashMap: HashMap<*, *>): MusicModel {
        val model = MusicModel()
        model.name = hashMap[SONG_NAME] as String?
        model.album = hashMap[ALBUM] as String?
        model.imageUrl = hashMap[IMAGE_URL] as String?
        model.author = hashMap[AUTHOR] as String?
        return model
    }

    fun byteArrayToBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}