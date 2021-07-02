package com.example.justbootupffs.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.justbootupffs.Entity.User;
import com.example.justbootupffs.R;
import com.example.justbootupffs.Service.UserService;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import static com.example.justbootupffs.Activity.LoginActivity.DATABASE_URL;
import static com.example.justbootupffs.Activity.LoginActivity.USER_DATABASE;

public class MainActivity extends AppCompatActivity {
    private TextView textViewAge, textViewName, textViewNameSidebar, textViewDescription;
    private Button buttonList, buttonListTeacher, buttonListStudent, buttonListMentor;
    private ImageView imageView, imageViewSidebar;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference userReference;
    private UserService currentUser;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_1:
                        Intent intentProfile = new Intent(MainActivity.this, ProfileActivity.class);
                        intentProfile.putExtra("user_id", currentUser.getId());
                        intentProfile.putExtra("edit", "1");
                        startActivity(intentProfile);
                        break;
                    case R.id.item_2:
                        Toast.makeText(MainActivity.this, "2", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item_3:
                        Toast.makeText(MainActivity.this, "3", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item_4:
                        Toast.makeText(MainActivity.this, "4", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item_5:
                        Toast.makeText(MainActivity.this, "5", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item_6:
                        Toast.makeText(MainActivity.this, "6", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = new UserService(dataSnapshot.getValue(User.class));
                textViewName.setText(currentUser.getName() + " " + currentUser.getSurname());
                textViewAge.setText("Возраст: " + currentUser.getAge());
                textViewDescription.setText(currentUser.getDescription());
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
        textViewAge = findViewById(R.id.textViewAge);
        textViewDescription = findViewById(R.id.textViewDescription);
        buttonList = findViewById(R.id.buttonList);
        buttonListStudent = findViewById(R.id.buttonListStudent);
        buttonListTeacher = findViewById(R.id.buttonListTeacher);
        buttonListMentor = findViewById(R.id.buttonListMentor);
        imageView = findViewById(R.id.imageViewMain);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        userReference = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference(USER_DATABASE + "/" + user.getUid());
        navigationView = findViewById(R.id.navViewSidebar);
        toolbar = findViewById(R.id.toolbarSidebar);
        drawerLayout = findViewById(R.id.drawerLayoutSidebar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull @NotNull View drawerView, float slideOffset) {
                textViewNameSidebar = findViewById(R.id.textViewNameSidebar);
                textViewNameSidebar.setText(currentUser.getName() + " " + currentUser.getSurname());
                imageViewSidebar = findViewById(R.id.imageViewSidebar);
                if (!TextUtils.isEmpty(currentUser.getProfilePicture())) {
                    Glide.with(MainActivity.this).load(currentUser.getProfilePicture()).into(imageViewSidebar);
                } else {
                    Glide.with(MainActivity.this).load("https://firebasestorage.googleapis.com/v0/b/justbootupffs.appspot.com/o/MXJXIJF.png?alt=media&token=1d26ea72-b6d5-4b82-a7b5-d0d41139e3b5").into(imageViewSidebar);
                }
            }

            @Override
            public void onDrawerOpened(@NonNull @NotNull View drawerView) {
                textViewNameSidebar = findViewById(R.id.textViewNameSidebar);
                textViewNameSidebar.setText(currentUser.getName() + " " + currentUser.getSurname());
                imageViewSidebar = findViewById(R.id.imageViewSidebar);
                if (!TextUtils.isEmpty(currentUser.getProfilePicture())) {
                    Glide.with(MainActivity.this).load(currentUser.getProfilePicture()).into(imageViewSidebar);
                } else {
                    Glide.with(MainActivity.this).load("https://firebasestorage.googleapis.com/v0/b/justbootupffs.appspot.com/o/MXJXIJF.png?alt=media&token=1d26ea72-b6d5-4b82-a7b5-d0d41139e3b5").into(imageViewSidebar);
                }
            }

            @Override
            public void onDrawerClosed(@NonNull @NotNull View drawerView) {
                return;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                return;
            }
        });
        toggle.syncState();
        navigationView.bringToFront();
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_logout);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        Button agree = dialog.findViewById(R.id.buttonDialogAgree);
        Button cancel = dialog.findViewById(R.id.buttonDialogCancel);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                dialog.dismiss();
                Intent intentLogout = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentLogout);
                Toast.makeText(MainActivity.this, "You logged out!", Toast.LENGTH_SHORT).show();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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

    public void onClickSidebarBack(View view) {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                dialog.show();
                break;
        }
        return true;
    }
}