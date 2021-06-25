package com.example.justbootupffs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.justbootupffs.Entity.User;
import com.example.justbootupffs.Service.UserService;
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
    private TextView textViewAge, textViewName, textViewSurname;
    private Button buttonLogout, buttonList, buttonListTeacher, buttonListStudent, buttonListMentor;
    private ImageView imageView;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    private UserService currentUser;

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
                currentUser = new UserService(dataSnapshot.getValue(User.class));
                textViewName.setText(currentUser.getName());
                textViewSurname.setText(currentUser.getSurname());
                textViewAge.setText("Возраст: " + currentUser.getAge());
                if (currentUser.isAdmin()) {
                    buttonList.setVisibility(View.VISIBLE);
                }
                if (currentUser.isMentor()) {
                    buttonListTeacher.setVisibility(View.VISIBLE);
                    buttonListStudent.setVisibility(View.VISIBLE);
                }
                if (currentUser.isTeacher()) {
                    buttonListMentor.setVisibility(View.VISIBLE);
                    buttonListStudent.setVisibility(View.VISIBLE);
                }
                if (!TextUtils.isEmpty(currentUser.getProfilePicture())) {
                    Glide.with(MainActivity.this).load(currentUser.getProfilePicture()).into(imageView);
                } else {
                    Glide.with(MainActivity.this).load("https://firebasestorage.googleapis.com/v0/b/justbootupffs.appspot.com/o/MXJXIJF.png?alt=media&token=1d26ea72-b6d5-4b82-a7b5-d0d41139e3b5").into(imageView);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(MainActivity.this, "DB error main",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init() {
        textViewName = findViewById(R.id.textViewName);
        textViewSurname = findViewById(R.id.textViewSurname);
        textViewAge = findViewById(R.id.textViewAge);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonList = findViewById(R.id.buttonList);
        buttonListStudent = findViewById(R.id.buttonListStudent);
        buttonListTeacher = findViewById(R.id.buttonListTeacher);
        buttonListMentor = findViewById(R.id.buttonListMentor);
        imageView = findViewById(R.id.imageViewMain);
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

    public void onClickProfile(View view) {
        Intent intentProfile = new Intent(this, ProfileActivity.class);
        intentProfile.putExtra("user_id", currentUser.getId());
        intentProfile.putExtra("edit", "1");
        startActivity(intentProfile);
    }

    public void onClickList(View view) {
        Intent intentList = new Intent(MainActivity.this, ListActivity.class);
        intentList.putExtra("filter", "all");
        startActivity(intentList);
    }

    public void onClickListTeacher(View view) {
        Intent intentList = new Intent(MainActivity.this, ListActivity.class);
        intentList.putExtra("filter", "teacher");
        startActivity(intentList);
    }

    public void onClickListMentor(View view) {
        Intent intentList = new Intent(MainActivity.this, ListActivity.class);
        intentList.putExtra("filter", "mentor");
        startActivity(intentList);
    }

    public void onClickListStudent(View view) {
        Intent intentList = new Intent(MainActivity.this, ListActivity.class);
        intentList.putExtra("filter", "student");
        startActivity(intentList);
    }
}