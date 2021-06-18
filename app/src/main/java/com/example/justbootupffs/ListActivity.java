package com.example.justbootupffs;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justbootupffs.Entity.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.justbootupffs.LoginActivity.DATABASE_URL;
import static com.example.justbootupffs.LoginActivity.USER_DATABASE;

public class ListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    public static final String LOG = "Test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        init();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<>();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    users.add(item.getValue(User.class));
                }
                ListRVAdapter adapter = new ListRVAdapter(ListActivity.this);
                adapter.setUsers(users);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(ListActivity.this, 1));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ListActivity.this, "DB error list",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init() {
        recyclerView = findViewById(R.id.listRV);
        databaseReference = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference(USER_DATABASE);
    }
}