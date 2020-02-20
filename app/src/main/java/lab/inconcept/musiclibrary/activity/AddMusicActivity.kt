package lab.inconcept.musiclibrary.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import lab.inconcept.musiclibrary.R
import lab.inconcept.musiclibrary.helper.Utils
import lab.inconcept.musiclibrary.model.MusicModel
import java.sql.Timestamp

class AddMusicActivity : AppCompatActivity(), View.OnClickListener {
    private var mSongNameView: AppCompatEditText? = null
    private var mSongAlbumView: AppCompatEditText? = null
    private var mSongArtistView: AppCompatEditText? = null
    private var mProgressBar: ProgressBar? = null
    private lateinit var mAddSongButton: AppCompatButton
    private lateinit var mAttachPictureButton: AppCompatButton
    private var mChildRef: DatabaseReference? = null
    private var mUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_music)
        init()
    }

    private fun requestDataPermission(): Boolean {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION)
            return false
        }
        return true
    }

    private fun showProgressBar(show: Boolean) {
        if (show) {
            mProgressBar!!.visibility = View.VISIBLE
            return
        }
        mProgressBar!!.visibility = View.GONE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                attachPicture()
            } else {
                Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun init() {
        mAttachPictureButton = findViewById(R.id.attach_picture)
        mSongNameView = findViewById(R.id.song_name)
        mSongAlbumView = findViewById(R.id.song_album)
        mSongArtistView = findViewById(R.id.song_artist)
        mAddSongButton = findViewById(R.id.add_button)
        mProgressBar = findViewById(R.id.progressBar)
        mChildRef = FirebaseDatabase.getInstance().reference.child(getString(R.string.songs_path))
        mAddSongButton.setOnClickListener(this)
        mAttachPictureButton.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val id = v.id
        Utils.hideKeyboard(v)
        when (id) {
            R.id.add_button -> {
                showProgressBar(true)
                addSong()
            }
            R.id.attach_picture -> attachPicture()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RESULT_LOAD_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                handleImage(data)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleImage(data: Intent?) {
        mUri = data!!.data
        if (mUri == null) {
            Toast.makeText(this, getString(R.string.error_load_image), Toast.LENGTH_SHORT).show()
            return
        }
        Toast.makeText(this, getString(R.string.image_added), Toast.LENGTH_SHORT).show()
    }

    private fun attachPicture() {
        if (!requestDataPermission()) {
            return
        }
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RESULT_LOAD_IMAGE)
    }

    private fun addSong() {
        val model = MusicModel()
        val nameEditable = mSongNameView!!.text
        if (nameEditable == null) {
            showProgressBar(false)
            return
        }
        val songName: String
        songName = nameEditable.toString()
        if (songName == "") {
            mSongNameView!!.error = getString(R.string.error_song_name)
            showProgressBar(false)
            return
        }
        model.name = songName
        model.album = mSongAlbumView!!.text.toString()
        model.author = mSongArtistView!!.text.toString()
        if (mUri != null) {
            val storageReference = FirebaseStorage.getInstance().reference
            val imageRef = storageReference.child("images/" + mUri!!.lastPathSegment)
            val uploadTask = imageRef.putFile(mUri!!)
            uploadTask.addOnFailureListener {
                Toast.makeText(this@AddMusicActivity, getString(R.string.error_upload_image),
                        Toast.LENGTH_SHORT).show()
                showProgressBar(false)
            }
            uploadTask.addOnSuccessListener {
                val downloadUrl = imageRef.downloadUrl
                downloadUrl.addOnSuccessListener { uri: Uri ->
                    val imgRef = uri.toString()
                    mChildRef!!.child(model.key!!).child(getString(R.string.image_url)).setValue(imgRef)
                    model.imageUrl = imgRef
                    showProgressBar(false)
                    finish()
                }
            }
        }
        model.imageUrl = " "
        val key = uniqueKey
        mChildRef!!.child(key).setValue(model)
        model.key = key
        if (mUri == null) {
            showProgressBar(false)
            finish()
        }
    }

    private val uniqueKey: String
        get() {
            val timestamp = Timestamp(System.currentTimeMillis())
            return timestamp.time.toString()
        }

    companion object {
        private const val REQUEST_PERMISSION = 1
        private const val RESULT_LOAD_IMAGE = 2
    }
}