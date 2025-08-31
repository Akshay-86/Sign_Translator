package com.example.voxignota;

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

public class LoginActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private Button loginBtn, signupBtn;
    private TextView skipBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Init FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        emailEt = findViewById(R.id.email);
        passwordEt = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.signupBtn);
        skipBtn = findViewById(R.id.skip);

        loginBtn.setOnClickListener(v -> loginUser());
        signupBtn.setOnClickListener(v -> signupUser());
        skipBtn.setOnClickListener(v -> signInAnonymously());
    }

    private void loginUser() {
        String email = emailEt.getText().toString().trim();
        String pass  = passwordEt.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Login success
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
    }

    private void signupUser() {
        String email = emailEt.getText().toString().trim();
        String pass  = passwordEt.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Signed up
                    Toast.makeText(LoginActivity.this, "Signup successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Signup failed: " + task.getException().getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Anonymous sign-in failed", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
