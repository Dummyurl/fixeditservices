package com.fixedit.fixitservices.Services;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.fixedit.fixitservices.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListOfServices extends AppCompatActivity {
    RecyclerView recyclerview;
    ServiceListAdapter adapter;
    private ArrayList<ServiceModel> itemList = new ArrayList<>();
    DatabaseReference mDatabase;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_services);
        back = findViewById(R.id.back);
        recyclerview = findViewById(R.id.recyclerview);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();


        recyclerview.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ServiceListAdapter(this, itemList);
        recyclerview.setAdapter(adapter);

        getDataFromDB();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void getDataFromDB() {
        mDatabase.child("Services").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    itemList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ServiceModel model = snapshot.getValue(ServiceModel.class);
                        if (model != null) {
                            itemList.add(model);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    itemList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
