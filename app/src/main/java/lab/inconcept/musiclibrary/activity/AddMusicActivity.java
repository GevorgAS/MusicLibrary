package lab.inconcept.musiclibrary.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Timestamp;

import lab.inconcept.musiclibrary.R;
import lab.inconcept.musiclibrary.helper.Utils;
import lab.inconcept.musiclibrary.model.MusicModel;

public class AddMusicActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_PERMISSION = 1;
    private static final int RESULT_LOAD_IMAGE = 2;
    private AppCompatEditText mSongNameView;
    private AppCompatEditText mSongAlbumView;
    private AppCompatEditText mSongArtistView;
    private AppCompatButton mAddSongButton;
    private ProgressBar mProgressBar;
    private AppCompatButton mAttachPictureButton;
    private DatabaseReference mChildRef;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_music);
        init();
    }

    private boolean requestDataPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            return false;
        }
        return true;
    }

    private void showProgressBar(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
            return;
        }
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    attachPicture();
                } else {
                    Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void init() {
        mAttachPictureButton = findViewById(R.id.attach_picture);
        mSongNameView = findViewById(R.id.song_name);
        mSongAlbumView = findViewById(R.id.song_album);
        mSongArtistView = findViewById(R.id.song_artist);
        mAddSongButton = findViewById(R.id.add_button);
        mProgressBar = findViewById(R.id.progressBar);
        mChildRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.songs_path));
        mAddSongButton.setOnClickListener(this);
        mAttachPictureButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        Utils.hideKeyboard(v);
        switch (id) {
            case R.id.add_button:
                showProgressBar(true);
                addSong();
                break;
            case R.id.attach_picture:
                attachPicture();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    handleImage(data);
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleImage(final Intent data) {
        mUri = data.getData();
        if (mUri == null) {
            Toast.makeText(this, getString(R.string.error_load_image), Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, getString(R.string.image_added), Toast.LENGTH_SHORT).show();
    }

    private void attachPicture() {
        if (!requestDataPermission()) {
            return;
        }
        final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    private void addSong() {
        final MusicModel model = new MusicModel();
        final Editable nameEditable = mSongNameView.getText();
        if (nameEditable == null) {
            showProgressBar(false);
            return;
        }
        String songName;
        songName = nameEditable.toString();
        if (songName.equals("")) {
            mSongNameView.setError(getString(R.string.error_song_name));
            showProgressBar(false);
            return;
        }
        model.setName(songName);
        model.setAlbum(mSongAlbumView.getText().toString());
        model.setAuthor(mSongArtistView.getText().toString());

        if (mUri != null) {
            final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference imageRef = storageReference.child("images/" + mUri.getLastPathSegment());
            final UploadTask uploadTask = imageRef.putFile(mUri);
            uploadTask.addOnFailureListener(e -> {
                Toast.makeText(AddMusicActivity.this, getString(R.string.error_upload_image),
                        Toast.LENGTH_SHORT).show();
                showProgressBar(false);
            });

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                final Task<Uri> downloadUrl = imageRef.getDownloadUrl();
                downloadUrl.addOnSuccessListener(uri -> {
                    final String imgRef = uri.toString();
                    mChildRef.child(model.getKey()).child(getString(R.string.image_url)).setValue(imgRef);
                    model.setImageUrl(imgRef);
                    showProgressBar(false);
                    finish();
                });
            });
        }
        model.setImageUrl(" ");
        final String key = getUniqueKey();
        mChildRef.child(key).setValue(model);
        model.setKey(key);
        if (mUri == null) {
            showProgressBar(false);
            finish();
        }
    }

    private String getUniqueKey() {
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return String.valueOf(timestamp.getTime());
    }
}
