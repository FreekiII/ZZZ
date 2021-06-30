package com.example.justbootupffs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static com.example.justbootupffs.LoginActivity.DATABASE_URL;
import static com.example.justbootupffs.LoginActivity.USER_DATABASE;

public class ProfileActivity extends AppCompatActivity {
    private EditText textName, textSurname, textAge, textDescription;
    private TextView textViewName, textViewSurname, textViewAge, textViewDescription;
    private CheckBox checkBoxAdmin, checkBoxTeacher, checkBoxStudent, checkBoxMentor;
    private ImageView imageView;
    private Button buttonSave, buttonChoose, buttonChangePassword, buttonChangeEmail;
    private DatabaseReference userReference;
    private StorageReference storageRef;
    private FirebaseUser user;
    private Uri imageUri;
    private UserService selectedUser, currentUser;
    private ActivityResultLauncher<Intent> mGetContent;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intentStart = getIntent();
        String id = intentStart.getStringExtra("user_id");
        String edit = intentStart.getStringExtra("edit");
        userReference = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference(USER_DATABASE + "/" + id);
        if (selectedUser == null) {
            userReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NotNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        selectedUser = new UserService(task.getResult().getValue(User.class));
                        if (TextUtils.equals(edit, "1") || currentUser.isAdmin()) {
                            textSurname.setVisibility(View.VISIBLE);
                            textDescription.setVisibility(View.VISIBLE);
                            textName.setVisibility(View.VISIBLE);
                            textAge.setVisibility(View.VISIBLE);
                            textAge.setText(selectedUser.getAge());
                            buttonSave.setVisibility(View.VISIBLE);
                            buttonChoose.setVisibility(View.VISIBLE);
                            if (selectedUser.getId() == currentUser.getId()) {
                                buttonChangeEmail.setVisibility(View.VISIBLE);
                                buttonChangePassword.setVisibility(View.VISIBLE);
                            }
                            textName.setText(selectedUser.getName());
                            textSurname.setText(selectedUser.getSurname());
                            if (currentUser.isAdmin()) {
                                checkBoxAdmin.setChecked(selectedUser.isAdmin());
                                checkBoxMentor.setChecked(selectedUser.isMentor());
                                checkBoxStudent.setChecked(selectedUser.isStudent());
                                checkBoxTeacher.setChecked(selectedUser.isTeacher());
                            } else {
                                checkBoxTeacher.setVisibility(View.GONE);
                                checkBoxStudent.setVisibility(View.GONE);
                                checkBoxMentor.setVisibility(View.GONE);
                                checkBoxAdmin.setVisibility(View.GONE);
                            }
                            textDescription.setText(selectedUser.getDescription());
                        } else {
                            textViewAge.setVisibility(View.VISIBLE);
                            textViewDescription.setVisibility(View.VISIBLE);
                            textViewName.setVisibility(View.VISIBLE);
                            textViewSurname.setVisibility(View.VISIBLE);
                            checkBoxAdmin.setVisibility(View.GONE);
                            checkBoxMentor.setVisibility(View.GONE);
                            checkBoxStudent.setVisibility(View.GONE);
                            checkBoxTeacher.setVisibility(View.GONE);
                            textViewName.setText(selectedUser.getName());
                            textViewSurname.setText(selectedUser.getSurname());
                            textViewDescription.setText(selectedUser.getDescription());
                            textViewAge.setText(selectedUser.getAge());
                        }
                        if (imageUri == null) {
                            if (!TextUtils.isEmpty(selectedUser.getProfilePicture())) {
                                Glide.with(ProfileActivity.this).load(selectedUser.getProfilePicture()).into(imageView);
                            } else {
                                Glide.with(ProfileActivity.this).load("https://firebasestorage.googleapis.com/v0/b/justbootupffs.appspot.com/o/NqtXx65.png?alt=media&token=73711395-bd5d-413a-85a8-310e32e97ebd").into(imageView);
                            }
                        }
                    }
                }
            });
        }
    }

    private void init() {
        textName = findViewById(R.id.editTextNameProfile);
        textSurname = findViewById(R.id.editTextSurnameProfile);
        textAge = findViewById(R.id.editTextAgeProfile);
        textDescription = findViewById(R.id.editTextDescriptionProfile);
        textViewName = findViewById(R.id.textViewNameProfile);
        textViewSurname = findViewById(R.id.textViewSurnameProfile);
        textViewAge = findViewById(R.id.textViewAgeProfile);
        textViewDescription = findViewById(R.id.textViewDescriptionProfile);
        checkBoxAdmin = findViewById(R.id.checkBoxAdmin);
        checkBoxMentor = findViewById(R.id.checkBoxMentor);
        checkBoxStudent = findViewById(R.id.checkBoxStudent);
        checkBoxTeacher = findViewById(R.id.checkBoxTeacher);
        buttonSave = findViewById(R.id.buttonSaveProfile);
        buttonChoose = findViewById(R.id.buttonChangeImage);
        buttonChangeEmail = findViewById(R.id.buttonChangeEmail);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        imageView = findViewById(R.id.imageViewProfile);
        toolbar = findViewById(R.id.toolbarProfile);
        setSupportActionBar(toolbar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageRef = FirebaseStorage.getInstance().getReference("Image");
        FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference(USER_DATABASE + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                currentUser = new UserService(task.getResult().getValue(User.class));
            }
        });
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Загрузка...");
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.RED));
        progressDialog.setProgress(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mGetContent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult activityResult) {
                        Intent data = activityResult.getData();
                        if (data != null && data.getData() != null) {
                            imageView.setImageURI(data.getData());
                            imageUri = data.getData();
                        } else {
                            Toast.makeText(ProfileActivity.this, "File selection failure",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadImage() {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        StorageReference fileRef = storageRef.child(UUID.randomUUID().toString() + selectedUser.getId());
        UploadTask uploadTask = fileRef.putBytes(out.toByteArray());
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            selectedUser.setProfilePicture(uri.toString());
                            userReference.setValue(selectedUser.getUser()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    selectedUser = null;
                                    progressDialog.dismiss();
                                    Intent intentUpdate = new Intent(ProfileActivity.this, MainActivity.class);
                                    startActivity(intentUpdate);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(ProfileActivity.this, "Update user failure",
                                            Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception exception) {
                            Toast.makeText(ProfileActivity.this, "Download link was not retrieved",
                                    Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "Upload failure",
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    public void onClickSaveProfile(View view) {
        String name = textName.getText().toString();
        if (TextUtils.isEmpty(name) || name.length() > 15) {
            textName.setError("Пожалуйста введите корректное имя");
            return;
        } else {
            selectedUser.setName(name);
        }
        String surname = textSurname.getText().toString();
        if (TextUtils.isEmpty(surname) || surname.length() > 20) {
            textSurname.setError("Пожалуйста введите корректную фамилию");
            return;
        } else {
            selectedUser.setSurname(surname);
        }
        String age = textAge.getText().toString();
        if (TextUtils.isEmpty(age) || age.length() > 3 || !TextUtils.isDigitsOnly(age)) {
            textAge.setError("Пожалуйста введите корректный возраст");
            return;
        } else {
            selectedUser.setAge(age);
        }
        String description = textDescription.getText().toString();
        if (TextUtils.isEmpty(description) || description.length() > 50) {
            textDescription.setError("Пожалуйста введите корректное описание");
            return;
        } else {
            selectedUser.setDescription(description);
        }
        if (currentUser.isAdmin()) {
            if (checkBoxTeacher.isChecked()) {
                selectedUser.setPrivilegesTeacher();
            } else {
                selectedUser.removePrivilegesTeacher();
            }
            if (checkBoxStudent.isChecked()) {
                selectedUser.setPrivilegesStudent();
            } else {
                selectedUser.removePrivilegesStudent();
            }
            if (checkBoxMentor.isChecked()) {
                selectedUser.setPrivilegesMentor();
            } else {
                selectedUser.removePrivilegesMentor();
            }
            if (checkBoxAdmin.isChecked()) {
                selectedUser.setPrivilegesAdmin();
            } else {
                selectedUser.removePrivilegesAdmin();
            }
        }
        progressDialog.show();
        if (imageUri != null && imageUri.toString() != "") {
            uploadImage();
        } else {
            userReference.setValue(selectedUser.getUser()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    selectedUser = null;
                    progressDialog.dismiss();
                    Intent intentUpdate = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intentUpdate);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(ProfileActivity.this, "Update user failure",
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    public void onClickChooseImage(View view) {
        selectedUser.setName(textName.getText().toString());
        selectedUser.setSurname(textSurname.getText().toString());
        selectedUser.setAge(textAge.getText().toString());
        selectedUser.setDescription(textDescription.getText().toString());
        Intent intentChoose = new Intent(Intent.ACTION_GET_CONTENT);
        intentChoose.setType("image/*");
        mGetContent.launch(intentChoose);
    }

    public void onClickChangeEmail(View view) {
        startActivity(new Intent(this, ChangeEmailActivity.class));
    }

    public void onClickChangePassword(View view) {
        startActivity(new Intent(this, ChangePasswordActivity.class));
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
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                break;
        }
        return true;
    }
}