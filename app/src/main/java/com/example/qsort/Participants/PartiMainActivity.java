package com.example.qsort.Participants;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.qsort.R;
import com.example.qsort.WelcomeActivity;

public class PartiMainActivity extends AppCompatActivity {

    TextView labelTextView;
    String categories, labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parti_main);

        Intent intent = getIntent();

        categories = intent.getExtras().getString("Categories");
        labels = intent.getExtras().getString("Labels");

        labelTextView.setText("Categories:\n"+
                categories+"\n\nLabels:\n"+labels);

    }

    public void backToEnterCode(View view){
        startActivity(new Intent(getApplicationContext(), PartiWelcomeActivity.class));
        finish();
    }



}
