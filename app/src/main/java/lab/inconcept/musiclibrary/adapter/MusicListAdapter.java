package lab.inconcept.musiclibrary.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import lab.inconcept.musiclibrary.R;
import lab.inconcept.musiclibrary.helper.Utils;
import lab.inconcept.musiclibrary.model.MusicModel;

public final class MusicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<MusicModel> mMusicList = new ArrayList<>();
    private SongListItemCallback mItemCallback;

    public void setCallback(final SongListItemCallback callback) {
        mItemCallback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new MusicItemViewHolder(inflater.inflate(R.layout.music_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final MusicItemViewHolder itemHolder = (MusicItemViewHolder) holder;
        final MusicModel model = mMusicList.get(position);
        Log.e("---", "OnBindViewHolder: " + position);
        itemHolder.mMusicNameView.setText(model.getName());
        itemHolder.mMusicAuthorView.setText(model.getAuthor());
        itemHolder.mMusicAlbumVIew.setText(model.getAlbum());
        itemHolder.mLayoutItem.setOnClickListener(v -> mItemCallback.onListItemClicked(model));
        if (model.getImageUrl().equals(" ")) {
            itemHolder.mMusicImageView.setImageResource(R.drawable.ic_library_music);
            return;
        }
        final StorageReference fileReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.getImageUrl());
        fileReference.getBytes(1024 * 1024).addOnSuccessListener(bytes -> {
            Log.e("---", "Position added bitmap: " + position);
            Log.e("---", "model image url: " + model.getImageUrl());
            itemHolder.mMusicImageView.setImageBitmap(Utils.byteArrayToBitmap(bytes));
        });
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }

    public void addItem(MusicModel model) {
        mMusicList.add(model);
        notifyDataSetChanged();
    }

    public void changeItem(MusicModel changedModel) {
        final int lastPosition = mMusicList.size() - 1;
        mMusicList.set(lastPosition, changedModel);
        notifyDataSetChanged();
    }

    private static final class MusicItemViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mLayoutItem;
        private AppCompatTextView mMusicNameView;
        private AppCompatTextView mMusicAuthorView;
        private AppCompatTextView mMusicAlbumVIew;
        private AppCompatImageView mMusicImageView;

        MusicItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mLayoutItem = itemView.findViewById(R.id.layoutItem);
            mMusicAlbumVIew = itemView.findViewById(R.id.albumName);
            mMusicAuthorView = itemView.findViewById(R.id.authorOfMusic);
            mMusicNameView = itemView.findViewById(R.id.musicName);
            mMusicImageView = itemView.findViewById(R.id.musicListIcon);
        }
    }
}
