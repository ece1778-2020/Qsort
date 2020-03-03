package com.example.qsort;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.qsort.Participants.PartiWelcomeActivity;
import com.example.qsort.UxResearcher.UxLoginActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnParticipant;
    private Button btnDesigner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnParticipant = findViewById(R.id.btnParti);
        btnDesigner = findViewById(R.id.btnDesigner);

        btnParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PartiWelcomeActivity.class));
                finish();
            }
        });

        btnDesigner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UxLoginActivity.class));
                finish();
            }
        });
    }
}
