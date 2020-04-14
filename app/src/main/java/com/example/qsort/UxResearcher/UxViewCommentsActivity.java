package com.example.qsort.UxResearcher;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qsort.Participants.PartiMainActivity;
import com.example.qsort.Project;
import com.example.qsort.R;
import com.example.qsort.TextComment;
import com.example.qsort.VoiceComment;
import com.example.qsort.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UxViewCommentsActivity extends AppCompatActivity {
    private static final String TAG = "UxViewCommentsActivity";
    private TextView projectName;
    private TextView whichLabel;
//    private Button btnBack;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String projectNameStr;
    private String whichLabelStr;
    private String projectIDStr;

    private String comment_text;
    private String timestamp;
    private String storageRef;

    ArrayList<TextComment> textCommentsList;
    ArrayList<VoiceComment> voiceCommentsList;

    private RecyclerView voiceRecyclerView;
    private RecyclerView textRecyclerView;
    RecyclerView.LayoutManager layoutManager_voice, layoutManager_text;
    UxTextCommentAdapter textCommentAdapter;
    UxVoiceCommentAdapter voiceCommentAdapter;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ux_view_comments);

        if(getIntent().hasExtra("project_name")){
            projectNameStr = getIntent().getStringExtra("project_name");
        }
        if(getIntent().hasExtra("which_label")){
            whichLabelStr = getIntent().getStringExtra("which_label");
        }
        if(getIntent().hasExtra("project_id")){
            projectIDStr = getIntent().getStringExtra("project_id");
        }

        context = UxViewCommentsActivity.this;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        projectName = findViewById(R.id.commentProjectName);
        whichLabel = findViewById(R.id.commentLabel);
//        btnBack = findViewById(R.id.backButton);
        textCommentsList  = new ArrayList<>();
        voiceCommentsList = new ArrayList<>();

        projectName.setText(projectNameStr);
        whichLabel.setText(whichLabelStr);

        voiceRecyclerView = findViewById(R.id.voiceCommentRecyclerView);
        textRecyclerView = findViewById(R.id.textCommentRecyclerView);
        layoutManager_voice = new GridLayoutManager(this, 1);
        layoutManager_text = new GridLayoutManager(this, 1);

        voiceRecyclerView.setLayoutManager(layoutManager_voice);
        textRecyclerView.setLayoutManager(layoutManager_text);

        Toast.makeText(UxViewCommentsActivity.this, "Loading comments",
                Toast.LENGTH_LONG).show();

        reloadComments();

    }

    private void reloadComments() {
        textCommentsList.clear();
        voiceCommentsList.clear();
        db.collection("projects").document(projectIDStr).collection("labels")
                .document(whichLabelStr).collection("text_comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                comment_text = document.getData().get("comment_text").toString();
                                timestamp = document.getData().get("timestamp").toString();
                                TextComment currentComment = new TextComment(timestamp, comment_text);
                                textCommentsList.add(currentComment);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        textCommentAdapter = new UxTextCommentAdapter(context, textCommentsList);
                        textRecyclerView.setAdapter(textCommentAdapter);
                        textRecyclerView.setHasFixedSize(true);
                    }
                });

        db.collection("projects").document(projectIDStr).collection("labels")
                .document(whichLabelStr).collection("voice_comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                storageRef = document.getData().get("storageRef").toString();
                                timestamp = document.getData().get("timestamp").toString();
                                VoiceComment currentComment = new VoiceComment(timestamp, storageRef);
                                voiceCommentsList.add(currentComment);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        voiceCommentAdapter = new UxVoiceCommentAdapter(context, voiceCommentsList);
                        voiceRecyclerView.setAdapter(voiceCommentAdapter);
                        voiceRecyclerView.setHasFixedSize(true);
                    }
                });
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}
