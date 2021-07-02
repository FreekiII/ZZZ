package com.example.justbootupffs.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.justbootupffs.Entity.User;
import com.example.justbootupffs.R;
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

import org.jetbrains.annotations.NotNull;

import static com.example.justbootupffs.Activity.LoginActivity.DATABASE_URL;
import static com.example.justbootupffs.Activity.LoginActivity.USER_DATABASE;

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
        editTextOldPassword = findViewById(R.id.editTextOldPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirm = findViewById(R.id.editTextNewPasswordConfirm);
        progressDialog = new ProgressDialog(ChangePasswordActivity.this);
        progressDialog.setMessage("Загрузка...");
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
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
        boolean flag = true;
        String oldPassword = editTextOldPassword.getText().toString();
        String newPassword = editTextNewPassword.getText().toString();
        String confirm = editTextConfirm.getText().toString();
        if (TextUtils.isEmpty(oldPassword) || !oldPassword.equals(currentUser.getPassword())) {
            editTextOldPassword.setError("Введён неверный пароль, пожалуйста введите ваш ТЕКУЩИЙ пароль.");
            flag = false;
        }
        if (newPassword.length() < 8 || newPassword.length() > 50) {
            editTextNewPassword.setError("Введён некоректный пароль, пожалуйста измените его (длина от 8 до 50 символов).");
            flag = false;
        } else {
            if (newPassword.equals(currentUser.getPassword())) {
                editTextNewPassword.setError("Вы ввели текущий пароль, пожалуйста введите НОВЫЙ пароль.");
                flag = false;
            }
        }
        if (!newPassword.equals(confirm)) {
            editTextConfirm.setError("Пароль отличается, пожалуйста повторно введите НОВЫЙ пароль.");
            flag = false;
        }
        if (flag) {
            progressDialog.show();
            user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    currentUser.setPassword(newPassword);
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
                    if (e.getClass().toString().equals("class com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException")) {
                        Toast.makeText(ChangePasswordActivity.this, "Для данной операции вам необходимо заного войти в вашу учётную запись!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, "Ошибка обновления пароля.",
                                Toast.LENGTH_SHORT).show();
                    }
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