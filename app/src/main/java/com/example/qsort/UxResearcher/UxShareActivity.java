package com.example.qsort.UxResearcher;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGSaver;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.qsort.R;
import com.example.qsort.TextComment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.WriterException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

import static android.widget.Toast.LENGTH_SHORT;

public class UxShareActivity extends AppCompatActivity {
    private static final String TAG = "UxShareActivity";

    TextView uniqueCodeTextView;
    EditText emailTextView;
    Button emailButton;
    String projectID, timestamp;
    ImageView QRcode;
    String QRstr = "";
    private FirebaseFirestore db;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ux_share);

        uniqueCodeTextView = findViewById(R.id.uniqueCodeTextView);
        QRcode = findViewById(R.id.QRImageView);
        emailButton = findViewById(R.id.emailButton);
        emailTextView = findViewById(R.id.emailTextView);

        Intent intent = getIntent();

        projectID = intent.getExtras().getString("Project ID");

        uniqueCodeTextView.setText(projectID);

        db = FirebaseFirestore.getInstance();


        db.collection("projects").document(projectID)
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot document, @Nullable FirebaseFirestoreException e) {
                QRstr = document.getData().get("QRcodeRef").toString();
                Log.d(TAG, document.getId() + " => " + document.getData());
                Glide.with(UxShareActivity.this).load(QRstr).into(QRcode);
            }
        });



        QRcode.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                saveQR(getViewBitmap(QRcode));
                return true;
            }
        });

    }

    private String saveQR(Bitmap viewBitmap) {

        if(!checkPermissionFromDevice())
            requestPermission();

        if (checkPermissionFromDevice()) {
            try {
                // get SD card path
                String sdCardPath = Environment.getExternalStorageDirectory().getPath();
                // path of the file
                System.out.println("sdcardpath"+sdCardPath);
                File file = new File(sdCardPath);
                File[] files = file.listFiles();

                if(files!=null){
                    for (int i = 0; i < files.length; i++) {
                        File file1 = files[i];
                        String name = file1.getName();
                        if (name.endsWith(projectID+".png")) {
                            boolean flag = file1.delete();
                        }
                    }
                }

                String filePath = sdCardPath + "/"+projectID+".png";

                file = new File(filePath);
                FileOutputStream os = new FileOutputStream(file);
                viewBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                os.flush();
                os.close();

                //insert the file into the system
                MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),
                        file.getAbsolutePath(), projectID+".png", null);

                //update the database after save the image
                Uri uri = Uri.fromFile(file);
                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

                Toast.makeText(getApplicationContext(),"QR code saved",Toast.LENGTH_SHORT).show();


                return filePath;
            }
            catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.toString());

                return "";
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"Please give permission to save the QR code to gallery.",Toast.LENGTH_SHORT).show();
            return "permission failed";
        }

    }

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_RECORD_AUDIO);
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

    private Bitmap getViewBitmap(View view) {
        if (view == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void shareEmail(View view){
        String emailList = emailTextView.getText().toString();
        String[] email = emailList.split(",");

        String link_val = QRstr;
        String body = "<a href=\"" + link_val + "\">" + QRstr + "</a>";


        String subject = "Your Qsort Invitation";
        String message = "Hello participant,\n\nYour unique code is "+projectID+"\n\nThis is a link to the QR code: \n"+Html.fromHtml(body)+"\n\nHave a nice day!";

        Intent intent  = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL,email);
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(android.content.Intent.EXTRA_TEXT,message);

//        intent.setType("message/rfc822");
        startActivity(intent);
        emailTextView.setText("");
    }


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), UxReportActivity.class);
        intent.putExtra("project_id",projectID);
        startActivity(intent);
    }


}
