package com.example.qsort.Participants;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qsort.R;
import com.example.qsort.UxResearcher.UxLoginActivity;
import com.example.qsort.UxResearcher.UxMainActivity;
import com.example.qsort.UxResearcher.UxRegisterActivity;
import com.example.qsort.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

public class CommentActivity extends AppCompatActivity {
    private static final String TAG = "TextComment";
    private EditText commentEdit;
    private TextView whichLabel;
    private TextView commentProgress;
    private Button btnSubmit;
    private Button btnVoiceStart;
    private Button btnVoiceStop;
    private ProgressBar progressBar;
    private ProgressDialog mProgress;
    private String timeStamp;
    private Boolean FLAG = false;

    private String whichLabelText = "";
    private String projectID;
    private String commentText = "";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    private MediaRecorder mediaRecorder;
//    private MediaPlayer mediaPlayer;
    private String mFilename = null;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1000;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parti_comment);

        Intent intent = getIntent();
        projectID = intent.getExtras().getString("Project_id");
        whichLabelText = intent.getExtras().getString("label");

        commentEdit = findViewById(R.id.textComment);
        whichLabel = findViewById(R.id.whichLabel);
        btnSubmit = findViewById(R.id.submitCommentBtn);
        btnVoiceStart = findViewById(R.id.voiceStartBtn);
        btnVoiceStop = findViewById(R.id.voiceStopBtn);
        commentProgress = findViewById(R.id.commentProgress);

        progressBar = findViewById(R.id.progressBarComment);
        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressBar.setVisibility(INVISIBLE);
        whichLabel.setText(whichLabelText);

        mFilename = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFilename += "/" + UUID.randomUUID().toString() + "_audio_record.3gp";

        if(!checkPermissionFromDevice())
            requestPermission();

//        btnVoice.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
////                    if (Build.VERSION.SDK_INT >= 23){
////                        checkRecordPermission();
////                    }
////                    else{
////                        startRecording();
////                        commentProgress.setText("Recording started..");
////                    }
//                    startRecording();
//                    commentProgress.setText("Recording started..");
//                }
//                // release the button
//                else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
//                    stopRecording();
//                    commentProgress.setText("Recording stopped..");
//                }
//                return false;
//            }
//        });

        btnVoiceStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissionFromDevice()){
                    btnVoiceStart.setEnabled(false);
                    btnVoiceStop.setEnabled(true);
                    setupMediaRecorder();
                    try{
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(CommentActivity.this, "Recording...", LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(CommentActivity.this, "You cannot leave a voice comment without permission.", LENGTH_SHORT).show();
                    requestPermission();
                }
            }
        });

        btnVoiceStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                btnVoiceStop.setEnabled(false);
                btnVoiceStart.setEnabled(true);
            }
        });

//        btnVoicePlay.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                btnVoiceFinish.setEnabled(true);
//                btnVoicePlay.setEnabled(false);
//                mediaPlayer = new MediaPlayer();
//                try{
//                    // storage reference stored in database
//                    mediaPlayer.setDataSource("");
//                    mediaPlayer.prepare();
//                }
//                catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                mediaPlayer.start();
//                Toast.makeText(CommentActivity.this, "Playing...", LENGTH_SHORT).show();
//            }
//        });
//
//        btnVoiceFinish.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                btnVoiceFinish.setEnabled(false);
//                btnVoicePlay.setEnabled(true);
//                if(mediaPlayer != null){
//                    mediaPlayer.stop();
//                    mediaPlayer.release();
////                    setupMediaRecorder();
//                }
//            }
//        });

    }

    private void setupMediaRecorder() {
        FLAG = true;
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(mFilename);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }


    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO
        }, MY_PERMISSIONS_RECORD_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", LENGTH_SHORT).show();
            }
                break;
        }
    }

//    private void startRecording() {
//        FLAG = true;
//
//        mediaRecorder = new MediaRecorder();
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mediaRecorder.setOutputFile(mFilename);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//        try {
//            mediaRecorder.prepare();
//        } catch (IOException e) {
//            Log.e(TAG, "prepare() failed");
//        }
//
//        mediaRecorder.start();
//    }

//    private void stopRecording() {
//        mediaRecorder.stop();
//        mediaRecorder.release();
////        mediaRecorder = null;
//    }

    private void uploadVoiceComment() {
        mProgress.setMessage("Uploading the audio..");
        mProgress.show();

        timeStamp = String.valueOf(System.currentTimeMillis());

        final StorageReference filepath = storageReference.child("Comments").child(whichLabelText+"_"+timeStamp+".3gp");
        Uri uri = Uri.fromFile(new File(mFilename));
        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                mProgress.dismiss();
                commentProgress.setText("Uploading finished.");
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: "+uri);
                        addCommentDB(uri, timeStamp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "OnFailure: ", e.getCause());
                    }
                });

            }
        });
    }

    private void uploadTextComment(String commentText) {
        // keep a link in firebase database
        timeStamp = String.valueOf(System.currentTimeMillis());
        Map<String, Object> comment = new HashMap<>();
        comment.put("comment_text", commentText);
        comment.put("timestamp", timeStamp);
        comment.put("type", "text");

        db.collection("projects").document(projectID).collection("labels").document(whichLabelText).collection("text_comments")
                .add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }


    private void addCommentDB(Uri uri, String timeStamp) {
        // keep a link in firebase database
        Map<String, Object> comment = new HashMap<>();
        comment.put("storageRef", String.valueOf(uri));
        comment.put("timestamp", timeStamp);
        comment.put("type", "voice");

        db.collection("projects").document(projectID).collection("labels").document(whichLabelText).collection("voice_comments")
                .add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

//    public void backToLogin(View view){
//        startActivity(new Intent(getApplicationContext(), PartiMainActivity.class));
//        finish();
//    }

//    @Override
//    public void onBackPressed()
//    {
//        startActivity(new Intent(getApplicationContext(), PartiMainActivity.class));
//        finish();
//    }

    public void submitComment(View view) {
        // check if the user has choose a profile image
        commentText = commentEdit.getText().toString();

        // check if the text field is empty or the voice is empty
        if (TextUtils.isEmpty(commentText) && FLAG == false ) {
            Toast.makeText(this, "Please give at least one type of comments.", LENGTH_SHORT).show();
            return;
        }

        // check if comment is too long
        if (commentText.length() > 200) {
            Toast.makeText(this, "Please give a shorter comment (less than 200 characters).", LENGTH_SHORT).show();
            return;
        }

        commentEdit.setEnabled(false);
        btnSubmit.setEnabled(false);
        btnVoiceStart.setEnabled(false);
        btnVoiceStop.setEnabled(false);

        Toast.makeText(CommentActivity.this, "Submitting the comment..",
                Toast.LENGTH_LONG).show();

        if(!TextUtils.isEmpty(commentText)){
            uploadTextComment(commentText);
        }
        if(FLAG == true){
            uploadVoiceComment();
            FLAG = false;
        }

        finish();

    }

//    private void checkRecordPermission(){
//        if (ContextCompat.checkSelfPermission(CommentActivity.this, Manifest.permission.RECORD_AUDIO)
//                != PackageManager.PERMISSION_GRANTED) {
//            //When permission is not granted by user, show them message
//            if (ActivityCompat.shouldShowRequestPermissionRationale(CommentActivity.this, Manifest.permission.RECORD_AUDIO)) {
//                Toast.makeText(CommentActivity.this,"Please grant permission to record audio.", LENGTH_SHORT).show();
//                //Give user option to still opt-in the permissions
//                ActivityCompat.requestPermissions(CommentActivity.this,
//                        new String[]{Manifest.permission.RECORD_AUDIO},
//                        MY_PERMISSIONS_RECORD_AUDIO);
//            }
//            else
//            {
//                // Show user dialog to grant permission to record audio
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.RECORD_AUDIO},
//                        MY_PERMISSIONS_RECORD_AUDIO);
//            }
//        }
//        //If permission is granted, then go ahead recording audio
//        else if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.RECORD_AUDIO)
//                == PackageManager.PERMISSION_GRANTED) {
//
//            //Go ahead with recording audio now
//            startRecording();
//            commentProgress.setText("Recording started..");
//        }
//    }


}
