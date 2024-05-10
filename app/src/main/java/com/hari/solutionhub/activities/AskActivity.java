package com.hari.solutionhub.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaCodecInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hari.solutionhub.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class AskActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Spinner spinner;
    private EditText questionBox;
    private ImageView imageView;
    private Button cancelBtn,postBtn;
    private String askedByName = "";
    private DatabaseReference askedByRef;
    private ProgressDialog loader;
    private String myUrl = "";
    StorageTask uploadTask;
    StorageReference storageReference;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String onlineUserId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ask);

        toolbar = findViewById(R.id.qs_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ask Question");

        spinner = findViewById(R.id.spinner);
        questionBox = findViewById(R.id.question_txt);
        imageView = findViewById(R.id.question_image);
        cancelBtn = findViewById(R.id.cancel_button);
        postBtn = findViewById(R.id.save_post);

        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        onlineUserId = user.getUid();
        askedByRef = FirebaseDatabase.getInstance().getReference("users").child(onlineUserId);
        askedByRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                askedByName = dataSnapshot.child("fullname").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        storageReference = FirebaseStorage.getInstance().getReference("questions");
        ArrayAdapter<String>adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.topics));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinner.getSelectedItem().equals("Select topic")){
                    Toast.makeText(AskActivity.this, "Please Select a valid topic", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/**");
                startActivityForResult(intent,1);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformDirectAction();
            }
        });

    }
    String getQuestionText(){
        return questionBox.getText().toString().trim();
    }
    String getTopic(){
        return spinner.getSelectedItem().toString();
    }
    String date = DateFormat.getDateInstance().format(new Date());
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("questions posts");

    private void PerformDirectAction(){
        if (getQuestionText().isEmpty()){
            questionBox.setError("Question required");

        } else if (getTopic().equals("Select topic")) {
            Toast.makeText(this, "Select Valid Topic", Toast.LENGTH_SHORT).show();

        } else if (!getQuestionText().isEmpty() && !getTopic().equals("") && imageUri == null) {
            uploadQuestionWithoutImage();

        } else if (!getQuestionText().isEmpty() && !getTopic().equals("") && imageUri != null) {
            uploadQuestionWithImage();

        }
    }
    private void startLoader(){
        loader.setMessage("posting your question");
        loader.setCanceledOnTouchOutside(false);
        loader.show();
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void  uploadQuestionWithoutImage(){
        startLoader();
        String postId = ref.push().getKey();
        HashMap<String, Object>hashMap = new HashMap<>();
        hashMap.put("postId",postId);
        hashMap.put("question",getQuestionText());
        hashMap.put("publisher",onlineUserId);
        hashMap.put("topic",getTopic());
        hashMap.put("askedBy",askedByName);
        hashMap.put("date",date);

        ref.child(postId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AskActivity.this, "Question Posted Successfully", Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                    startActivity(new Intent(AskActivity.this, HomeActivity.class));
                    finish();
                }else {
                    Toast.makeText(AskActivity.this, "Could not upload image"+task.getException().toString(),
                            Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                }
            }
        });

    }
    private void uploadQuestionWithImage() {
        startLoader();
        final StorageReference fileref;
        fileref = storageReference.child(System.currentTimeMillis() + '.' + getFileExtension(imageUri));
        uploadTask = fileref.putFile(imageUri);
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isCanceled()){
                    throw   task.getException();

                }
                return fileref.getDownloadUrl();
            }
        }).addOnCompleteListener(new  OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){

                    Uri download = (Uri) task.getResult();
                    myUrl = download.toString();
                    String postId = ref.push().getKey();
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("postid",postId);
                    hashMap.put("question",getQuestionText());
                    hashMap.put("publisher",onlineUserId);
                    hashMap.put("topic",getTopic());
                    hashMap.put("askedby",askedByName);
                    hashMap.put("questionimage",myUrl);
                    hashMap.put("date",date);

                    ref.child(postId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Toast.makeText(AskActivity.this, "Question Posted Successfully", Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                                startActivity(new Intent(AskActivity.this,HomeActivity.class));
                                finish();


                            }else {
                                Toast.makeText(AskActivity.this, "could not upload image "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                loader.dismiss();

                            }
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AskActivity.this, "Failed to upload the question", Toast.LENGTH_SHORT).show();

            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== -1 && resultCode ==RESULT_OK && data != null){

            imageUri = data.getData();
            imageView.setImageURI(imageUri);


        }
    }
}