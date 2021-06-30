package com.example.justbootupffs;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.justbootupffs.Entity.User;
import com.example.justbootupffs.Service.UserService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.justbootupffs.LoginActivity.DATABASE_URL;
import static com.example.justbootupffs.LoginActivity.USER_DATABASE;

public class ListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    public static final String LOG = "Test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intentStart = getIntent();
        String filter = intentStart.getStringExtra("filter");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<User> users = new ArrayList<>();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    User user = item.getValue(User.class);
                    UserService userService = new UserService(user);
                    if (isFiltered(userService, filter)) {
                        users.add(user);
                    }
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

    private boolean isFiltered(UserService userService, String filter) {
        switch (filter) {
            case "all": return true;
            case "teacher": if (userService.isTeacher()) {
                return true;
            } else {
                return false;
            }
            case "student": if (userService.isStudent()) {
                return true;
            } else {
                return false;
            }
            case "mentor": if (userService.isMentor()) {
                return true;
            } else {
                return false;
            }
            default: return false;
        }
    }

    private void init() {
        recyclerView = findViewById(R.id.listRV);
        databaseReference = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference(USER_DATABASE);
        toolbar = findViewById(R.id.toolbarList);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_toolbar2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                startActivity(new Intent(ListActivity.this, MainActivity.class));
                break;
        }
        return true;
    }
}