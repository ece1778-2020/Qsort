package com.example.qsort.UxResearcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.example.qsort.Participants.CommentActivity;
import com.example.qsort.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class UxReportActivity extends AppCompatActivity {

    private static final String TAG = "UxReportActivity";
    private ImageView projectView;
    private TextView projectName;
    private TextView projectTime;
    private TextView projectPartiNum;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;
    private String timestamp;
    private RecyclerView labelButtonRecyclerView;
    private TextView labelLabel, categoryRank;
    private TextView reportUniqueCode;
    private Button labelButton;
    private Switch enableSwitch;
    private Button viewCommentsButton;

    ArrayList<String> list = new ArrayList<>();

    RecyclerView.LayoutManager layoutManager;
    private String project_id = "";
    String noParticipants;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ux_report);

        if(getIntent().hasExtra("project_id")){
            project_id = getIntent().getStringExtra("project_id");
        }

        context = UxReportActivity.this;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        projectView = findViewById(R.id.reportProjectImage);
        projectName = findViewById(R.id.reportProjectName);
        projectTime = findViewById(R.id.reportProjectTime);
        projectPartiNum = findViewById(R.id.reportPartiNum);
        labelButtonRecyclerView = findViewById(R.id.labelButtonRecyclerView);
        reportUniqueCode = findViewById(R.id.reportUniqueCode);
        labelButton = findViewById(R.id.labelReportButton);
        enableSwitch = findViewById(R.id.enableSwitch);
        viewCommentsButton = findViewById(R.id.viewCommentsButton);


        labelLabel = findViewById(R.id.reportResult);
        categoryRank = findViewById(R.id.categoriesRank);
        displayProjectsInfo();
        displayProjectLabelButton();

        if(labelLabel.getText().toString().equals("Card sorting results")){
            viewCommentsButton.setVisibility(View.INVISIBLE);
            viewCommentsButton.setEnabled(false);
        }

        db.collection("projects").document(project_id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                enableSwitch.setChecked(documentSnapshot.getBoolean("Availability"));
            }
        });

        enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    db.collection("projects").document(project_id)
                            .update("Availability",true);
                }
                else{
                    db.collection("projects").document(project_id)
                            .update("Availability",false);
                }
            }
        });

    }

//    public void backToMain(View view){
//        startActivity(new Intent(getApplicationContext(),UxMainActivity.class));
//        finish();
//    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(getApplicationContext(),UxMainActivity.class));
        finish();
    }

    public void displayProjectsInfo(){

        final DocumentReference documentReference = db.collection("projects").document(project_id);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                noParticipants = documentSnapshot.getData().get("Participants").toString();
                projectName.setText(documentSnapshot.getData().get("Project Name").toString());
                projectPartiNum.setText(documentSnapshot.getData().get("Participants").toString());
                reportUniqueCode.setText(documentSnapshot.getData().get("Project ID").toString());

                timestamp = documentSnapshot.getData().get("timestamp").toString();

                Long timestampLong = Long.parseLong(String.valueOf(timestamp));
                if (timestampLong < 10000000000L) {
                    timestampLong = timestampLong * 1000;
                }

                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(Long.parseLong(String.valueOf(timestampLong)));
                String sd = sf.format(new Date(Long.parseLong(String.valueOf(timestampLong))));
                projectTime.setText(sd);



                Glide.with(UxReportActivity.this).load(documentSnapshot.getString("Project Picture")).into(projectView);
            }
        });

    }

    public void displayProjectLabelButton(){

        db.collection("projects").document(project_id)
                .collection("labels")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){

                    for(QueryDocumentSnapshot document : task.getResult()){
                        String labelButtonMap = document.getId();
                        list.add(labelButtonMap);
                    }

                    labelButtonRecyclerView = findViewById(R.id.labelButtonRecyclerView);
                    UxReportButtonAdapter myAdapter = new UxReportButtonAdapter(UxReportActivity.this,list,project_id);
                    layoutManager = new GridLayoutManager(UxReportActivity.this, 1);
                    labelButtonRecyclerView.setLayoutManager(layoutManager);
                    labelButtonRecyclerView.setAdapter(myAdapter);
                }

            }
        });


    }

    public void displayRank(View view){

        viewCommentsButton.setVisibility(View.VISIBLE);
        viewCommentsButton.setEnabled(true);
        final Button labelButton = (Button)view;
        final String labelButtonText = labelButton.getText().toString();

        db.collection("projects").document(project_id)
                .collection("labels").document(labelButtonText)
                .collection("categories")
                .orderBy("value",Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        StringBuilder categoriesRank = new StringBuilder();
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String value = document.getData().get("value").toString();
                                categoriesRank.append(document.getId()+": "+value+"/"+noParticipants+"\n\n");
                            }
                            labelLabel.setText(labelButtonText);
                            categoryRank.setText(categoriesRank);

                        }
                    }
                });

        // store state
//        recyclerViewState = labelButtonRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    public void shareCode(View view){

        Intent intent = new Intent(getApplicationContext(),UxShareActivity.class);

        intent.putExtra("Project ID",userId+"_"+timestamp);
        // start the activity
        startActivity(intent);
    }

    public void deleteProject(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm delete your project?");
        builder.setMessage("You are about to delete your project and all records. Do you really want to proceed ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You've choosen to delete all records", Toast.LENGTH_SHORT).show();
                db.collection("projects").document(project_id).delete();
                startActivity(new Intent(getApplicationContext(),UxMainActivity.class));
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You've changed your mind to delete all records", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();

    }

    public void viewComments(View view){
        Intent intent = new Intent(getApplicationContext(), UxViewCommentsActivity.class);
        intent.putExtra("project_name",projectName.getText());
        intent.putExtra("which_label",labelLabel.getText());
        intent.putExtra("project_id",project_id);
        startActivity(intent);
    }

}
