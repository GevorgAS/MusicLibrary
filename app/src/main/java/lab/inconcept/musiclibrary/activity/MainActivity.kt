package lab.inconcept.musiclibrary.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import lab.inconcept.musiclibrary.R
import lab.inconcept.musiclibrary.adapter.MusicListAdapter
import lab.inconcept.musiclibrary.adapter.SongListItemCallback
import lab.inconcept.musiclibrary.fragment.DetailedMusicFragment
import lab.inconcept.musiclibrary.helper.Utils
import lab.inconcept.musiclibrary.model.MusicModel
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, SongListItemCallback {
    private var mFragmentContainer: LinearLayoutCompat? = null
    private var mRecyclerView: RecyclerView? = null
    private var mListAdapter: MusicListAdapter? = null
    private var mAddFab: FloatingActionButton? = null
    private var mRootRef: DatabaseReference? = null
    private var mChildRef: DatabaseReference? = null
    private var mExitAlert: AlertDialog? = null

    private val userVisibleFragment: Fragment?
        get() {
            val fragmentManager = supportFragmentManager
            val fragmentList = fragmentManager.fragments
            if (fragmentList.size == 0) {
                return null
            }
            for (i in fragmentList.indices.reversed()) {
                val fragment = fragmentList[i]
                if (fragment == null || !fragment.userVisibleHint) {
                    continue
                }
                return fragment
            }
            throw RuntimeException(getString(R.string.smt_went_wrong))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        mFragmentContainer = findViewById(R.id.fragment_content)
        mRecyclerView = findViewById(R.id.recyclerView)
        mAddFab = findViewById(R.id.addFab)
        mListAdapter = MusicListAdapter()
        mListAdapter!!.setCallback(this)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
        mRecyclerView!!.setHasFixedSize(true)
        mAddFab!!.setOnClickListener(this)
        mRecyclerView!!.adapter = mListAdapter
        mRootRef = FirebaseDatabase.getInstance().reference
        mChildRef = mRootRef!!.child(getString(R.string.songs_path))
        subscribeChildDatabase()
    }

    private fun subscribeChildDatabase() {
        mChildRef!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val hashMap = dataSnapshot.value as HashMap<*, *>?
                val model = Utils.getModel(hashMap!!)
                mListAdapter!!.addItem(model)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                val hashMap = dataSnapshot.value as HashMap<*, *>?
                val model = Utils.getModel(hashMap!!)
                mListAdapter!!.changeItem(model)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun showFragment(show: Boolean) {
        if (show) {
            mFragmentContainer!!.visibility = View.VISIBLE
            mRecyclerView!!.visibility = View.GONE
            mAddFab!!.visibility = View.GONE
        } else {
            mFragmentContainer!!.visibility = View.GONE
            mRecyclerView!!.visibility = View.VISIBLE
            mAddFab!!.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.addFab -> {
                val intent = Intent(this, AddMusicActivity::class.java)
                startActivity(intent)
            }
        }

    }

    override fun onBackPressed() {
        val fragment: Fragment?
        try {
            fragment = userVisibleFragment
        } catch (e: RuntimeException) {
            e.printStackTrace()
            super.onBackPressed()
            return
        }

        if (fragment != null) {
            showFragment(false)
            Objects.requireNonNull<ActionBar>(supportActionBar).setDisplayHomeAsUpEnabled(false)
        }
        supportFragmentManager.popBackStack()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exit_to_app -> exitApplicationDialog()
            android.R.id.home -> onBackPressed()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.app_menu, menu)
        return true
    }

    override fun onListItemClicked(model: MusicModel?) {
        val fragment = DetailedMusicFragment.getInstance(model)
        openFragment(fragment)
        showFragment(true)
    }

    private fun exitApplicationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.alert_exit)
        builder.setMessage(R.string.alert_message)
        builder.setPositiveButton(R.string.alert_ok) { _, _ -> signOut() }

        builder.setNegativeButton(R.string.alert_cancel) { _, _ -> mExitAlert!!.dismiss() }
        mExitAlert = builder.create()
        builder.show()
    }

    private fun openFragment(fragment: DetailedMusicFragment) {
        val transaction = supportFragmentManager.beginTransaction()
        val tag = fragment.javaClass.simpleName
        transaction.add(R.id.fragment_content, fragment, tag)
        transaction.addToBackStack(tag)
        transaction.commit()
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        val signOut = Intent(this, EmailPasswordActivity::class.java)
        startActivity(signOut)
    }
}
