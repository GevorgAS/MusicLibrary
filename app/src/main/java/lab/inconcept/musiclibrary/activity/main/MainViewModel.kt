package lab.inconcept.musiclibrary.activity.main

import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import lab.inconcept.musiclibrary.R
import lab.inconcept.musiclibrary.activity.AddMusicActivity
import lab.inconcept.musiclibrary.activity.email_password.EmailPasswordActivity
import lab.inconcept.musiclibrary.helper.Utils
import lab.inconcept.musiclibrary.model.MusicModel
import java.lang.ref.WeakReference
import java.util.*

class MainViewModel : ViewModel() {
    private lateinit var weak: WeakReference<MainFragment>
    private lateinit var childRef: DatabaseReference
    private lateinit var childListener: ChildEventListener

    val songs = MutableLiveData<List<MusicModel>>()
    val addSong = MutableLiveData<MusicModel>()
    val changedSong = MutableLiveData<MusicModel>()

    fun onCreate(fragment: MainFragment) {
        weak = WeakReference(fragment)
        childRef = FirebaseDatabase.getInstance().reference.child("songs")
        subscribeChildDatabase()
    }

    fun onOptionsItemSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.exit_to_app -> exitApplicationDialog()
        }
    }

    private fun signOut() {
        val fragment = weak.get() ?: throw IllegalStateException("Context is null")
        FirebaseAuth.getInstance().signOut()
        val signOut = Intent(fragment.context, EmailPasswordActivity::class.java)
        fragment.startActivity(signOut)
    }

    fun onFabClick(v: View) {
        val intent = Intent(v.context, AddMusicActivity::class.java)
        v.context.startActivity(intent)

    }

    private fun exitApplicationDialog() {
        val fragment = weak.get() ?: throw IllegalStateException("Context is null")
        val builder = AlertDialog.Builder(fragment.requireContext())
        builder.setTitle(R.string.alert_exit)
        builder.setMessage(R.string.alert_message)
        builder.setPositiveButton(R.string.alert_ok) { _, _ -> signOut() }

        builder.setNegativeButton(R.string.alert_cancel) { dialog, _ -> dialog.dismiss() }
        builder.create()
        builder.show()
    }

    private fun subscribeChildDatabase() {

        fun subscribeToChild() {
            childListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val hashMap = dataSnapshot.value as HashMap<*, *>?
                    val model = Utils.getModel(dataSnapshot.key, hashMap!!)
                    addSong.value = model
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                    val hashMap = dataSnapshot.value as HashMap<*, *>?
                    val model = Utils.getModel(dataSnapshot.key, hashMap!!)
                    changedSong.value = model
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
            childRef.addChildEventListener(childListener)
        }

        FirebaseDatabase.getInstance().reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val hashMap = dataSnapshot.value as HashMap<*, *>?
                val model = Utils.getModels(hashMap!!)
                songs.value = model?.sortedBy { it.key }
                FirebaseDatabase.getInstance().reference.removeEventListener(this)
                subscribeToChild()
            }
        })
    }

    fun onDestroyView() {
        childRef.removeEventListener(childListener)
        songs.value = null
        addSong.value = null
        changedSong.value = null
    }
}

@BindingAdapter("recAllData")
fun recAllData(view: RecyclerView, data: List<MusicModel>?) {
    data?.let {
        if (view.adapter is BindableAdapter<*>) {
            (view.adapter as BindableAdapter<MusicModel>).setData(data)
        }
    }
}

@BindingAdapter("recData")
fun recData(view: RecyclerView, model: MusicModel?) {

    model?.let {
        if (view.adapter is BindableAdapter<*>) {
            (view.adapter as BindableAdapter<MusicModel>).updateData(model)
        }
    }
}

@BindingAdapter("changeData")
fun changeData(view: RecyclerView, changedData: MusicModel?) {

    changedData?.let {
        if (view.adapter is BindableAdapter<*>) {
            (view.adapter as BindableAdapter<MusicModel>).changeItem(changedData)
        }
    }
}

@BindingAdapter("recAdapter")
fun recAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
    recyclerView.adapter = adapter
}