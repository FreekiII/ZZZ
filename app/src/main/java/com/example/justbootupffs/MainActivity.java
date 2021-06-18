package com.example.justbootupffs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.justbootupffs.Entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.justbootupffs.LoginActivity.DATABASE_URL;
import static com.example.justbootupffs.LoginActivity.USER_DATABASE;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private Button buttonLogout;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                User info = dataSnapshot.getValue(User.class);
//                textView.setText("Hi, " + info.username + "!");
                textView.setText(user.getEmail());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "DB error main",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init() {
        textView = findViewById(R.id.textView);
        buttonLogout = findViewById(R.id.buttonLogout);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference(USER_DATABASE + "/" + user.getUid());
    }

    //TODO Shift this button to menu
    public void onClickLogout(View view) {
        mAuth.signOut();
        Intent intentLogout = new Intent(this, LoginActivity.class);
        startActivity(intentLogout);
        Toast.makeText(this, "You logged out!", Toast.LENGTH_SHORT).show();
    }

    public void onClickList(View view) {
        Intent intentList = new Intent(MainActivity.this, ListActivity.class);
        startActivity(intentList);
    }
}