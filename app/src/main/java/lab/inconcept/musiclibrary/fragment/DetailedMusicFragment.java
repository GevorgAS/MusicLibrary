package lab.inconcept.musiclibrary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import lab.inconcept.musiclibrary.R;
import lab.inconcept.musiclibrary.helper.BundleHelper;
import lab.inconcept.musiclibrary.helper.Utils;
import lab.inconcept.musiclibrary.model.MusicModel;

public class DetailedMusicFragment extends Fragment {
    private AppCompatImageView mSongImageView;
    private AppCompatTextView mSongNameView;
    private AppCompatTextView mSongArtistView;
    private AppCompatTextView mSongAlbumView;

    private DetailedMusicFragment() {
    }

    public static DetailedMusicFragment getInstance(MusicModel model) {
        final DetailedMusicFragment fragment = new DetailedMusicFragment();
        BundleHelper.bundleMusicModel(fragment, model);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detialed_music, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        setupActionBar();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity == null) {
            return;
        }
        final ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
    }


    private void initView(final View view) {
        mSongImageView = view.findViewById(R.id.song_image);
        mSongNameView = view.findViewById(R.id.song_name);
        mSongArtistView = view.findViewById(R.id.song_artist);
        mSongAlbumView = view.findViewById(R.id.song_album);
        final MusicModel model = BundleHelper.getMusicModel(this);
        if (model == null) {
            return;
        }
        mSongNameView.setText(model.getName());
        mSongArtistView.setText(model.getAuthor());
        mSongAlbumView.setText(model.getAlbum());

        if (model.getImageUrl().equals(" ")) {
            return;
        }
        final StorageReference fileReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.getImageUrl());
        fileReference.getBytes(1024 * 1024).addOnSuccessListener(bytes ->
                mSongImageView.setImageBitmap(Utils.byteArrayToBitmap(bytes)));
    }
}
