package com.example.qsort.UxResearcher;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.qsort.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class UxRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EmailPassword";
    private EditText email;
    private EditText password;
    private EditText password2;
    private EditText username;
    private EditText bio;
    private Button btnSignup;
    private ProgressBar progressBar;

    private String strEmail = "";
    private String strPass = "";
    private String strPass2 = "";
    private String strUsername = "";
    private String strBio = "";
    private String userId = "";

    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ux_register);
        initView();
    }

    private void initView() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);
        btnSignup = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBarRegister);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnSignup.setOnClickListener(this);
        progressBar.setVisibility(INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                register();
                break;
        }
    }

    private void register() {
        // check if the user has choose a profile image
        strEmail = email.getText().toString();
        strPass = password.getText().toString();
        strPass2 = password2.getText().toString();
        strUsername = username.getText().toString();
        strBio = bio.getText().toString();

        // check if the textfields are empty
        if (TextUtils.isEmpty(strEmail) | TextUtils.isEmpty(strPass) | TextUtils.isEmpty(strPass2)
                | TextUtils.isEmpty(strUsername) | TextUtils.isEmpty(strBio)) {
            Toast.makeText(this, "Please fill all the blanks.", Toast.LENGTH_SHORT).show();
            return;
        }
        // check if password == password2
        if (!strPass.equals(strPass2)) {
            Toast.makeText(this, "Please confirm the password.", Toast.LENGTH_SHORT).show();
            password.getText().clear();
            password2.getText().clear();
            return;
        }
        // check if bio is too long
        if (strBio.length() > 100) {
            Toast.makeText(this, "Please give a shorter biography (less than 100 characters).", Toast.LENGTH_SHORT).show();
            bio.getText().clear();
            return;
        }
        // check if bio is too long
        if (strPass.length()<6) {
            Toast.makeText(this, "Please give a longer password (more than 6characters).", Toast.LENGTH_SHORT).show();
            bio.getText().clear();
            return;
        }

        email.setEnabled(false);
        password.setEnabled(false);
        password2.setEnabled(false);
        username.setEnabled(false);
        bio.setEnabled(false);
        btnSignup.setEnabled(false);

        Toast.makeText(UxRegisterActivity.this, "Signing up!",
                Toast.LENGTH_LONG).show();

        mAuth.createUserWithEmailAndPassword(strEmail, strPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(VISIBLE);

                            // register success, save user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(UxRegisterActivity.this, "Account created!",
                                    Toast.LENGTH_LONG).show();
                            userId = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = db.collection("Users").document(userId);

                            // Create a new user with a first and last name
                            Map<String, Object> user = new HashMap<>();
                            user.put("email", strEmail);
                            user.put("username", strUsername);
                            user.put("bio", strBio);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + userId);
                                }
                            });

                            timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(UxRegisterActivity.this, UxMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 3000);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(UxRegisterActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();

                            email.setEnabled(true);
                            password.setEnabled(true);
                            password2.setEnabled(true);
                            username.setEnabled(true);
                            bio.setEnabled(true);
                            btnSignup.setEnabled(true);
                            progressBar.setVisibility(INVISIBLE);

                        }
                    }
                });
    }
}
