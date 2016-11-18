package com.hotel.hotelroomreservation.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hotel.hotelroomreservation.R;
import com.hotel.hotelroomreservation.adapters.PhotoAdapter;
import com.hotel.hotelroomreservation.model.Addresses;
import com.hotel.hotelroomreservation.model.Constants;

import java.util.ArrayList;
import java.util.List;

public class PhotoListActivity extends AppCompatActivity {
    private Firebase dbReference;
    private RecyclerView.Adapter bitmapAdapter;
    private RecyclerView photosRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        toolbarInitialize();

        photosRecyclerView = (RecyclerView) findViewById(R.id.photos_recycler_view);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //TODO singletone
        //getSystemService("")
        dbReference = new Firebase(Addresses.FIREBASE_URL);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //TODO extract to method
        final List<String> hotelPhotosUrls = new ArrayList<>();
        //TODO move to BaseActivity
        dbReference.child(Constants.PHOTOS_KEY).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot urlSnapshot : snapshot.getChildren()) {
                    hotelPhotosUrls.add((String) urlSnapshot.getValue());
                }
                bitmapAdapter = new PhotoAdapter(hotelPhotosUrls);
                photosRecyclerView.setAdapter(bitmapAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i("tag", firebaseError.getMessage());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO move to baseActivity
    private void toolbarInitialize() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolBar.setTitle(R.string.title_gallery);
        setSupportActionBar(toolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
