package com.example.qsort.UxResearcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.qsort.R;
import com.example.qsort.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UxLoginActivity extends AppCompatActivity {

    private static final String TAG = "LOGIN";

    private ProgressBar progressBar;
    private EditText emailField;
    private EditText passwordField;
    private Button btnSignin;
    private Button btnRegister;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ux_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Views
        emailField = findViewById(R.id.useremail);
        passwordField = findViewById(R.id.password);
        progressBar = findViewById(R.id.loading);
        btnSignin = findViewById(R.id.login);
        btnRegister = findViewById(R.id.register);
        progressBar.setVisibility(View.INVISIBLE);

        // Buttons
        if(mAuth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), UxMainActivity.class));
        }
        else{
            btnSignin.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    progressBar.setVisibility(View.VISIBLE);
                    if(emailField.getText().toString().isEmpty() || passwordField.getText().toString().isEmpty()){
                        Toast.makeText(UxLoginActivity.this, "Please fill all the blanks.",
                                Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    else{
                        btnSignin.setVisibility(View.INVISIBLE);
                        mAuth.signInWithEmailAndPassword(emailField.getText().toString(),
                                passwordField.getText().toString()).
                                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressBar.setVisibility(View.GONE);
                                        btnSignin.setVisibility(View.VISIBLE);
                                        if(task.isSuccessful()){
                                            Toast.makeText(UxLoginActivity.this, "Logged in!",
                                                    Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(UxLoginActivity.this, UxMainActivity.class));
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(UxLoginActivity.this, task.getException().getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                }
            });

            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), UxRegisterActivity.class));
                    finish();
                }
            });
        }
    }

    public void backToWelcome(View view){
        startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
        finish();
    }
}
