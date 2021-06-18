package com.example.justbootupffs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.justbootupffs.Entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    private EditText textUsername, textPassword, textDescription;
    private ImageView imageView;
    private Button buttonSave, buttonChoose;
    private DatabaseReference userReference;
    private StorageReference storageRef;
    private Uri imageUri;
    private User selectedUser;
    private ActivityResultLauncher<Intent> mGetContent;

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
        userReference = FirebaseDatabase.getInstance(DATABASE_URL)
                .getReference(USER_DATABASE + "/" + id);
        userReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NotNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    selectedUser = task.getResult().getValue(User.class);
                    textUsername.setText(selectedUser.username);
                    textPassword.setText(selectedUser.password);
                    textDescription.setText(selectedUser.description);
                    if (imageUri == null) {
                        if (!TextUtils.isEmpty(selectedUser.profilePicture)) {
                            Glide.with(ProfileActivity.this).load(selectedUser.profilePicture).into(imageView);
                        } else {
                            Glide.with(ProfileActivity.this).load("https://firebasestorage.googleapis.com/v0/b/justbootupffs.appspot.com/o/Image%2F0Nk3Jxv.png?alt=media&token=3af1c410-08c8-4562-841b-2ed246fbe332").into(imageView);
                        }
                    }
                }
            }
        });
//        userReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                selectedUser = dataSnapshot.getValue(User.class);
//                textUsername.setText(selectedUser.username);
//                textPassword.setText(selectedUser.password);
//                textDescription.setText(selectedUser.description);
//                if (selectedUser.profilePicture != null && selectedUser.profilePicture != "") {
//                    Glide.with(ProfileActivity.this).load(selectedUser.profilePicture).into(imageView);
//                } else {
//                    Glide.with(ProfileActivity.this).load("https://firebasestorage.googleapis.com/v0/b/justbootupffs.appspot.com/o/Image%2F0Nk3Jxv.png?alt=media&token=3af1c410-08c8-4562-841b-2ed246fbe332").into(imageView);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Toast.makeText(ProfileActivity.this, "DB error profile",
//                        Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private void init() {
        textUsername = findViewById(R.id.editTextUsernameProfile);
        textPassword = findViewById(R.id.editTextPasswordProfile);
        textDescription = findViewById(R.id.editTextDescriptionProfile);
        buttonSave = findViewById(R.id.buttonSaveProfile);
        buttonChoose =findViewById(R.id.buttonChangeImage);
        imageView = findViewById(R.id.imageViewProfile);
        storageRef = FirebaseStorage.getInstance().getReference("Image");
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
        StorageReference fileRef = storageRef.child(UUID.randomUUID().toString() + selectedUser.id);
        UploadTask uploadTask = fileRef.putBytes(out.toByteArray());
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            selectedUser.profilePicture = uri.toString();
                            userReference.setValue(selectedUser);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(ProfileActivity.this, "Download link was not retrieved",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "Upload failure",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onClickSaveProfile(View view) {
        selectedUser.username = textUsername.getText().toString();
        selectedUser.password = textPassword.getText().toString();
        selectedUser.description = textDescription.getText().toString();
        if (imageUri != null && imageUri.toString() != "") {
            uploadImage();
        }
    }

    public void onClickChooseImage(View view) {
        Intent intentChoose = new Intent(Intent.ACTION_GET_CONTENT);
        intentChoose.setType("image/*");
        mGetContent.launch(intentChoose);
    }
}