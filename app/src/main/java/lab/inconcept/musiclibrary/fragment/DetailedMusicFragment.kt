package lab.inconcept.musiclibrary.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.google.firebase.storage.FirebaseStorage
import lab.inconcept.musiclibrary.R
import lab.inconcept.musiclibrary.helper.BundleHelper.bundleMusicModel
import lab.inconcept.musiclibrary.helper.BundleHelper.getMusicModel
import lab.inconcept.musiclibrary.helper.Utils
import lab.inconcept.musiclibrary.model.MusicModel

class DetailedMusicFragment private constructor() : Fragment() {
    private lateinit var mSongImageView: AppCompatImageView
    private lateinit var mSongNameView: AppCompatTextView
    private lateinit var mSongArtistView: AppCompatTextView
    private lateinit var mSongAlbumView: AppCompatTextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_detialed_music, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)
        setupActionBar()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> activity!!.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {
        val activity = activity as AppCompatActivity? ?: return
        val actionBar = activity.supportActionBar ?: return
        actionBar.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

    private fun initView(view: View) {
        mSongImageView = view.findViewById(R.id.song_image)
        mSongNameView = view.findViewById(R.id.song_name)
        mSongArtistView = view.findViewById(R.id.song_artist)
        mSongAlbumView = view.findViewById(R.id.song_album)
        val model = getMusicModel(this) ?: return
        mSongNameView.text = model.name
        mSongArtistView.text = model.author
        mSongAlbumView.text = model.album
        if (model.imageUrl == null || model.imageUrl == " ") {
            return
        }
        val fileReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.imageUrl!!)
        fileReference.getBytes(1024 * 1024.toLong()).addOnSuccessListener { bytes: ByteArray ->
            mSongImageView.setImageBitmap(Utils.byteArrayToBitmap(bytes))
        }
    }

    companion object {
        fun getInstance(model: MusicModel?): DetailedMusicFragment {
            val fragment = DetailedMusicFragment()
            bundleMusicModel(fragment, model)
            return fragment
        }
    }
}