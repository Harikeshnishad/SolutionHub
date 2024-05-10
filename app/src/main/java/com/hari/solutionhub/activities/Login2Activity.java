package com.hari.solutionhub.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hari.solutionhub.R;

import java.util.Objects;

public class Login2Activity extends AppCompatActivity {
    private TextView question;
    private EditText emailEd,passwordEd;
    private Button login;
    private ProgressDialog loader;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login2);

        question = findViewById(R.id.logQuestion);
        emailEd = findViewById(R.id.logEmail);
        passwordEd = findViewById(R.id.logPassword);
        login = findViewById(R.id.logBtn);
        loader = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login2Activity.this, RegistrationActivity.class);
                startActivity(intent);
            }

        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEd.getText().toString();
                String password = passwordEd.getText().toString();
                if (TextUtils.isEmpty(email)){
                    emailEd.setError("Email is required");

                }if (TextUtils.isEmpty(password)){
                    passwordEd.setError("Password is required");
                }else {
                    loader.setMessage("Login in progress");
                    loader.setCanceledOnTouchOutside(false);
                  //  loader.show();

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Login2Activity.this, "Login is successful. logged in as "
                                        + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login2Activity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            }else {
                                Toast.makeText(Login2Activity.this, "Login failed"+ task.getException().toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}