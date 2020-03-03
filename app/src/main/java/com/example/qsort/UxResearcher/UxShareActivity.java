package com.example.qsort.UxResearcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.qsort.R;

public class UxShareActivity extends AppCompatActivity {

    TextView uniqueCodeTextView;
    EditText emailTextView;
    Button QRcodeButton, emailButton;
    String projectID, timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ux_share);

        uniqueCodeTextView = findViewById(R.id.uniqueCodeTextView);
        QRcodeButton = findViewById(R.id.QRcodeButton);
        emailButton = findViewById(R.id.emailButton);
        emailTextView = findViewById(R.id.emailTextView);

        Intent intent = getIntent();

        projectID = intent.getExtras().getString("Project ID");

        uniqueCodeTextView.setText(projectID);


    }

    public void shareEmail(View view){
        String emailList = emailTextView.getText().toString();
        String[] email = emailList.split(",");

        String subject = "Your Qsort Invitation";
        String message = "Hello participant,\nYour unique code is "+projectID+"\nHave a nice day!";

        Intent intent  = new Intent(Intent.ACTION_SEND);
        intent.putExtra(intent.EXTRA_EMAIL,email);
        intent.putExtra(intent.EXTRA_SUBJECT,subject);
        intent.putExtra(intent.EXTRA_TEXT,message);

        intent.setType("message/rfc822");
        startActivity(intent);
        emailTextView.setText("");
    }

    public void backToMain(View view){
        Intent intent = new Intent(getApplicationContext(), UxReportActivity.class);
        intent.putExtra("project_id",projectID);
        startActivity(intent);
    }



}
