package lab.inconcept.musiclibrary.adapter;

import lab.inconcept.musiclibrary.model.MusicModel;

public interface SongListItemCallback {
    void onListItemClicked(final MusicModel model);
}
