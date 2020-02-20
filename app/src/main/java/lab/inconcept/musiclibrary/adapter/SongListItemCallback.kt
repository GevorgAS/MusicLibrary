package lab.inconcept.musiclibrary.adapter

import lab.inconcept.musiclibrary.model.MusicModel

interface SongListItemCallback {
    fun onListItemClicked(model: MusicModel?)
}