package com.hari.solutionhub.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hari.solutionhub.R;
import com.squareup.picasso.Downloader;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private EditText username,fullname,email, password;
    private Button regBtn;
    private TextView question;

    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private ProgressDialog loader;
    private String onlineUserId = "";
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        fullname = findViewById(R.id.reFullName);
        password = findViewById(R.id.regPassword);
        email = findViewById(R.id.regEmail);
        question = findViewById(R.id.regQuestion);
        regBtn = findViewById(R.id.regButton);

        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/**");
                startActivityForResult(intent,1);
            }
        });

        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, Login2Activity.class);
                startActivity(intent);
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = username.getText().toString();
                String fullName = fullname.getText().toString();
                String emailText = email.getText().toString();
                String paasText = password.getText().toString();

                if (TextUtils.isEmpty(userName)){
                    username.setError("username is required");
                }if (TextUtils.isEmpty(fullName)){
                    fullname.setError("fullname is required");
                }if (TextUtils.isEmpty(emailText)){
                    email.setError("email is required");
                }if (TextUtils.isEmpty(paasText)){
                    password.setError("password is required");
                }

                if (resultUri == null){
                    Toast.makeText(RegistrationActivity.this, "Profile Image is required", Toast.LENGTH_SHORT).show();
                }
                else {

                    loader.setMessage("Registration in progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.createUserWithEmailAndPassword(emailText,paasText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()){
                                Toast.makeText(RegistrationActivity.this, "Registration failed" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }else {
                                String onlineUserId = mAuth.getCurrentUser().getUid();
                                reference = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserId);
                                Map hashMap = new HashMap<>();
                                hashMap.put("username",userName);
                                hashMap.put("fullname",fullName);
                                hashMap.put("id",onlineUserId);
                                hashMap.put("email",emailText);
                                reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(RegistrationActivity.this, "Details set Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(RegistrationActivity.this, "Failed to upload data" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                        //loader.dismiss();
                                        finish();

                                    }
                                });
                                if (resultUri != null){

                                    final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile image").child(onlineUserId);
                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),resultUri);
                                    } catch (FileNotFoundException e) {
                                        throw new RuntimeException(e);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
                                    byte[] data = byteArrayOutputStream.toByteArray();
                                    UploadTask uploadTask = filePath.putBytes(data);
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            if (taskSnapshot.getMetadata().getReference() != null){

                                                Task<Uri>result = taskSnapshot.getStorage().getDownloadUrl();
                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String imageUrl = uri.toString();
                                                        Map hashMap = new HashMap<>();
                                                        hashMap.put("profileImageUrl",imageUrl);
                                                        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if (task.isSuccessful()){

                                                                    Toast.makeText(RegistrationActivity.this, "Profile Image added successfully",
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                                else {
                                                                    Toast.makeText(RegistrationActivity.this, "Process failed" + task.getException().toString(),
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                        finish();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                    loader.dismiss();
                                }
                            }
                        }
                    });

                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && requestCode == RESULT_OK && data != null){

            resultUri = data.getData();
            profileImage.setImageURI(resultUri);
        }
    }
}