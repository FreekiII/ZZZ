package com.example.justbootupffs;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.justbootupffs.Entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {
    public static final String USER_DATABASE = "User";
    public static final String DATABASE_URL = "https://justbootupffs-default-rtdb.europe-west1.firebasedatabase.app/";
    private EditText textPassword, textEmail, textName, textSurname, textAge;
    private FrameLayout frameLayout;
    private Button buttonSignIn, buttonRegister, buttonConfirm, buttonCancel;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_form);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            changeFragment(new LoginFragment());
            buttonRegister.setVisibility(View.VISIBLE);
            buttonSignIn.setVisibility(View.VISIBLE);
        } else {
            Intent intentAuthorized = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intentAuthorized);
        }
    }

    private void init() {
        frameLayout = findViewById(R.id.parentFL);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonConfirm = findViewById(R.id.buttonConfirm);
        buttonCancel = findViewById(R.id.buttonCancel);
        toolbar = findViewById(R.id.toolbarLogin);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference(USER_DATABASE);
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.parentFL, fragment);
        fragmentTransaction.commit();
    }

    public void onClickSignIn(View view) {
        ConstraintLayout constraintLayout = (ConstraintLayout)frameLayout.getChildAt(0);
        textEmail = (EditText)constraintLayout.getViewById(R.id.editTextEmail);
        textPassword = (EditText)constraintLayout.getViewById(R.id.editTextPassword);
        String email = textEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            textEmail.setError("Пожалуйста введите емайл");
            return;
        }
        String password = textPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            textPassword.setError("Пожалуйста введите пароль");
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Authentication is successful!",
                                        Toast.LENGTH_SHORT).show();
                                Intent intentAuthorized = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intentAuthorized);
                            } else {
                                mAuth.signOut();
                                Toast.makeText(LoginActivity.this, "Please verify your e-mail",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Введены неверные данные",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onClickRegister (View view) {
        changeFragment(new RegisterFragment());
        buttonRegister.setVisibility(View.GONE);
        buttonSignIn.setVisibility(View.GONE);
        buttonConfirm.setVisibility(View.VISIBLE);
        buttonCancel.setVisibility(View.VISIBLE);
    }

    public void onClickConfirm (View view) {
        ConstraintLayout constraintLayout = (ConstraintLayout)frameLayout.getChildAt(0);
        textEmail = (EditText)constraintLayout.getViewById(R.id.editTextEmail);
        textPassword = (EditText)constraintLayout.getViewById(R.id.editTextPassword);
        textName = (EditText)constraintLayout.getViewById(R.id.editTextName);
        textSurname = (EditText)constraintLayout.getViewById(R.id.editTextSurname);
        textAge = (EditText)constraintLayout.getViewById(R.id.editTextAge);
        String email = textEmail.getText().toString();
        //TODO Validation of email
        String password = textPassword.getText().toString();
        if (textPassword.getText().length() < 8 || textPassword.getText().length() > 50) {
            textPassword.setError("Введён некоректный пароль, пожалуйста измените его (длина от 8 до 50 символов).");
            return;
        }
        String name = textName.getText().toString();
        if (TextUtils.isEmpty(name) || name.length() > 15) {
            textName.setError("Пожалуйста введите корректное имя");
            return;
        }
        String surname = textSurname.getText().toString();
        if (TextUtils.isEmpty(surname) || surname.length() > 20) {
            textSurname.setError("Пожалуйста введите корректную фамилию");
            return;
        }
        String age = textAge.getText().toString();
        if (TextUtils.isEmpty(age) || age.length() > 3 || !TextUtils.isDigitsOnly(age)) {
            textAge.setError("Пожалуйста введите корректный возраст");
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(view.getContext(), "Вы успешно зарегестрированы!",
                                    Toast.LENGTH_SHORT).show();

                            sendEmailVerification();

                            String uid = mAuth.getCurrentUser().getUid();
                            databaseReference.child(uid).setValue(new User(uid, name, surname, age, email, password)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    mAuth.signOut();
                                    onClickCancel(view);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(), "User save failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(view.getContext(), "Регистрация провалена, пожалуйста проверьте введённые данные.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onClickCancel(View view) {
        changeFragment(new LoginFragment());
        buttonRegister.setVisibility(View.VISIBLE);
        buttonSignIn.setVisibility(View.VISIBLE);
        buttonConfirm.setVisibility(View.GONE);
        buttonCancel.setVisibility(View.GONE);
    }

    public void sendEmailVerification() {
        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Подтвердите вашу почту.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Mail confirmation failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
