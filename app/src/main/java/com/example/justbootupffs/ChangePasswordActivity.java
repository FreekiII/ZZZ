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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.justbootupffs.Entity.User;
import com.example.justbootupffs.Service.UserService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

import static com.example.justbootupffs.LoginActivity.DATABASE_URL;
import static com.example.justbootupffs.LoginActivity.USER_DATABASE;

public class ChangePasswordActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseUser user;
    private UserService currentUser;
    private DatabaseReference userReference;
    private EditText editTextOldPassword, editTextNewPassword, editTextConfirm;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbarChangePassword);
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
        editTextConfirm = findViewById(R.id.editTextOldPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirm = findViewById(R.id.editTextNewPasswordConfirm);
        progressDialog = new ProgressDialog(ChangePasswordActivity.this);
        progressDialog.setMessage("Загрузка...");
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.RED));
        progressDialog.setProgress(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    public void onClickChangePasswordCancel(View view) {
        Intent intentUpdate = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
        intentUpdate.putExtra("user_id", currentUser.getId());
        intentUpdate.putExtra("edit", "1");
        startActivity(intentUpdate);
    }

    public void onClickChangePasswordConfirm(View view) {
        progressDialog.show();
        boolean flag = true;
        if (!editTextOldPassword.getText().equals(currentUser.getPassword())) {
            editTextOldPassword.setError("Введён неверный пароль, пожалуйста введите ваш ТЕКУЩИЙ пароль.");
            flag = false;
        }
        if (editTextNewPassword.getText().equals(currentUser.getPassword())) {
            editTextNewPassword.setError("Вы ввели текущий пароль, пожалуйста введите НОВЫЙ пароль.");
            flag = false;
        }
        if (!editTextNewPassword.getText().equals(editTextConfirm.getText())) {
            editTextConfirm.setError("Пароль отличается, пожалуйста повторно введите НОВЫЙ пароль.");
            flag = false;
        }
        if (editTextNewPassword.getText().length() < 8 || editTextNewPassword.getText().length() > 50) {
            editTextNewPassword.setError("Введён некоректный пароль, пожалуйста измените его (длина от 8 до 50 символов).");
            flag = false;
        }
        if (flag) {
            String password = editTextNewPassword.getText().toString();
            user.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    userReference.setValue(currentUser.getUser()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            Toast.makeText(ChangePasswordActivity.this, "Пароль успешно обновлён!",
                                    Toast.LENGTH_SHORT).show();
                            Intent intentUpdate = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
                            intentUpdate.putExtra("user_id", currentUser.getId());
                            intentUpdate.putExtra("edit", "1");
                            startActivity(intentUpdate);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(ChangePasswordActivity.this, "Update user failure",
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
                    Toast.makeText(ChangePasswordActivity.this, "Ошибка обновления пароля, пожайлуста заново войдите в вашу учётную запись!",
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
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
                startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                break;
        }
        return true;
    }
}