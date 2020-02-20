package lab.inconcept.musiclibrary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import lab.inconcept.musiclibrary.R
import lab.inconcept.musiclibrary.helper.Utils
import lab.inconcept.musiclibrary.model.MusicModel
import java.util.*

class MusicListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mMusicList: MutableList<MusicModel> = ArrayList()
    private var mItemCallback: SongListItemCallback? = null
    fun setCallback(callback: SongListItemCallback?) {
        mItemCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MusicItemViewHolder(inflater.inflate(R.layout.music_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as MusicItemViewHolder
        val model = mMusicList[position]
        itemHolder.mMusicNameView.text = model.name
        itemHolder.mMusicAuthorView.text = model.author
        itemHolder.mMusicAlbumVIew.text = model.album
        itemHolder.mLayoutItem.setOnClickListener { mItemCallback!!.onListItemClicked(model) }
        if (model.imageUrl == null || model.imageUrl == " ") {
            itemHolder.mMusicImageView.setImageResource(R.drawable.ic_library_music)
            return
        }
        val fileReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.imageUrl!!)
        fileReference.getBytes(1024 * 1024.toLong()).addOnSuccessListener { bytes: ByteArray ->
            itemHolder.mMusicImageView.setImageBitmap(Utils.byteArrayToBitmap(bytes))
        }
    }

    override fun getItemCount(): Int {
        return mMusicList.size
    }

    fun addItem(model: MusicModel) {
        mMusicList.add(model)
        notifyDataSetChanged()
    }

    fun changeItem(changedModel: MusicModel) {
        val lastPosition = mMusicList.size - 1
        mMusicList[lastPosition] = changedModel
        notifyDataSetChanged()
    }

    private class MusicItemViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mLayoutItem: RelativeLayout = itemView.findViewById(R.id.layoutItem)
        var mMusicNameView: AppCompatTextView = itemView.findViewById(R.id.musicName)
        var mMusicAuthorView: AppCompatTextView = itemView.findViewById(R.id.authorOfMusic)
        var mMusicAlbumVIew: AppCompatTextView = itemView.findViewById(R.id.albumName)
        val mMusicImageView: AppCompatImageView = itemView.findViewById(R.id.musicListIcon)

    }
}