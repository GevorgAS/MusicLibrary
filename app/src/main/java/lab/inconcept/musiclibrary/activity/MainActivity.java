package lab.inconcept.musiclibrary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import lab.inconcept.musiclibrary.R;
import lab.inconcept.musiclibrary.adapter.MusicListAdapter;
import lab.inconcept.musiclibrary.adapter.SongListItemCallback;
import lab.inconcept.musiclibrary.fragment.DetailedMusicFragment;
import lab.inconcept.musiclibrary.helper.Utils;
import lab.inconcept.musiclibrary.model.MusicModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SongListItemCallback {
    private LinearLayoutCompat mFragmentContainer;
    private RecyclerView mRecyclerView;
    private MusicListAdapter mListAdapter;
    private FloatingActionButton mAddFab;
    private DatabaseReference mRootRef;
    private DatabaseReference mChildRef;
    private AlertDialog mExitAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mFragmentContainer = findViewById(R.id.fragment_content);
        mRecyclerView = findViewById(R.id.recyclerView);
        mAddFab = findViewById(R.id.addFab);
        mListAdapter = new MusicListAdapter();
        mListAdapter.setCallback(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mAddFab.setOnClickListener(this);
        mRecyclerView.setAdapter(mListAdapter);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mChildRef = mRootRef.child(getString(R.string.songs_path));
        subscribeChildDatabase();
    }

    private void subscribeChildDatabase() {
        mChildRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("---", "Child added!");
                final HashMap hashMap = (HashMap) dataSnapshot.getValue();
                final MusicModel model = Utils.getModel(hashMap);
//                mRecyclerView.getRecycledViewPool().clear();
                mListAdapter.addItem(model);
                Log.e("---", model.getImageUrl());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("---", "Child changed!" + s);
                final HashMap hashMap = (HashMap) dataSnapshot.getValue();
                final MusicModel model = Utils.getModel(hashMap);
                mListAdapter.changeItem(model);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.e("---", "Child removed!");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.e("---", "Child moved!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showFragment(boolean show) {
        if (show) {
            mFragmentContainer.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mAddFab.setVisibility(View.GONE);
        } else {
            mFragmentContainer.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAddFab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.addFab:
                final Intent intent = new Intent(this, AddMusicActivity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        Fragment fragment;
        try {
            fragment = getUserVisibleFragment();
        } catch (RuntimeException e) {
            e.printStackTrace();
            super.onBackPressed();
            return;
        }
        if (fragment != null) {
            showFragment(false);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        }
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.exit_to_app:
                exitApplicationDialog();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public void onListItemClicked(final MusicModel model) {
        final DetailedMusicFragment fragment = DetailedMusicFragment.getInstance(model);
        openFragment(fragment);
        showFragment(true);
    }

    private void exitApplicationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_exit);
        builder.setMessage(R.string.alert_message);
        builder.setPositiveButton(R.string.alert_ok, (dialog, which) -> {
            signOut();
        });

        builder.setNegativeButton(R.string.alert_cancel, (dialog, which) -> mExitAlert.dismiss());
        mExitAlert = builder.create();
        builder.show();
    }

    private void openFragment(final DetailedMusicFragment fragment) {
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        final String tag = fragment.getClass().getSimpleName();
        transaction.add(R.id.fragment_content, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    private Fragment getUserVisibleFragment() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final List<Fragment> fragmentList = fragmentManager.getFragments();
        if (fragmentList.size() == 0) {
            return null;
        }
        for (int i = fragmentList.size() - 1; i >= 0; i--) {
            final Fragment fragment = fragmentList.get(i);
            if (fragment == null || !fragment.getUserVisibleHint()) {
                continue;
            }
            return fragment;
        }
        throw new RuntimeException(getString(R.string.smt_went_wrong));
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        final Intent signOut = new Intent(this, EmailPasswordActivity.class);
        startActivity(signOut);
    }
}
