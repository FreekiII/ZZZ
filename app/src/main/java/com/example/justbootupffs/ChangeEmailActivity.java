package com.example.justbootupffs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.justbootupffs.Entity.User;
import com.example.justbootupffs.Service.UserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import static com.example.justbootupffs.LoginActivity.DATABASE_URL;
import static com.example.justbootupffs.LoginActivity.USER_DATABASE;

public class ChangeEmailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseUser user;
    private UserService currentUser;
    private DatabaseReference userReference;
    private ProgressDialog progressDialog;
    private EditText editTextChangeEmail;
    private TextView textViewOldEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        textViewOldEmail.setText(user.getEmail());
    }

    private void init() {
        toolbar = findViewById(R.id.toolbarChangeEmail);
        setSupportActionBar(toolbar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference(USER_DATABASE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                currentUser = new UserService(task.getResult().getValue(User.class));
                userReference = FirebaseDatabase.getInstance(DATABASE_URL)
                        .getReference(USER_DATABASE + "/" + user.getUid());
            }
        });
        progressDialog = new ProgressDialog(ChangeEmailActivity.this);
        progressDialog.setMessage("Загрузка...");
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.RED));
        progressDialog.setProgress(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        editTextChangeEmail = findViewById(R.id.editTextNewEmail);
        textViewOldEmail = findViewById(R.id.textViewCurrentEmail);
    }

    public void onClickChangeEmailCancel(View view) {
        Intent intentUpdate = new Intent(ChangeEmailActivity.this, ProfileActivity.class);
        intentUpdate.putExtra("user_id", currentUser.getId());
        intentUpdate.putExtra("edit", "1");
        startActivity(intentUpdate);
    }

    public void onClickChangeEmailConfirm(View view) {
        progressDialog.show();
        String email = editTextChangeEmail.getText().toString();
        user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                userReference.setValue(currentUser.getUser()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(ChangeEmailActivity.this, "Почта успешно обновлена!",
                                Toast.LENGTH_SHORT).show();
                        Intent intentUpdate = new Intent(ChangeEmailActivity.this, ProfileActivity.class);
                        intentUpdate.putExtra("user_id", currentUser.getId());
                        intentUpdate.putExtra("edit", "1");
                        startActivity(intentUpdate);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ChangeEmailActivity.this, "Update user failure",
                                Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.println(Log.ERROR, "ERROR", e.getMessage());
                Log.println(Log.ERROR, "ERROR", e.getClass().toString());
                Toast.makeText(ChangeEmailActivity.this, "Ошибка обновления почты, пожайлуста заново войдите в вашу учётную запись!",
                        Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
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
                startActivity(new Intent(ChangeEmailActivity.this, MainActivity.class));
                break;
        }
        return true;
    }
}