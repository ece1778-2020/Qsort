package com.example.qsort.Participants;

//import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.qsort.R;
import com.example.qsort.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import dmax.dialog.SpotsDialog;

public class ScanQRActivity extends AppCompatActivity {
    CameraView cameraView;
    Button btnDetect;
//    AlertDialog waitingDialogue;
    String projectID;
    FirebaseFirestore firebaseFirestore;

    String categories, labels;
    Boolean project_availability;
    String project_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parti_scan);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        cameraView = (CameraView)findViewById(R.id.cameraView);
        btnDetect = (Button)findViewById(R.id.btnDetect);
//        waitingDialogue = new SpotsDialog.Builder().setContext(this).setMessage("Please wait").setCancelable(false).build();
        firebaseFirestore = FirebaseFirestore.getInstance();
        btnDetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.start();
                cameraView.captureImage();
            }
        });

        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {
            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

//                waitingDialogue.show();
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, cameraView.getWidth(), cameraView.getHeight(), false);
                cameraView.stop();

                runDetect(bitmap);
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });


    }

    private void runDetect(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(
                                FirebaseVisionBarcode.FORMAT_QR_CODE)
                        .build();

        final FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector();

        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        // Task completed successfully
                        // [START_EXCLUDE]
                        // [START get_barcodes]
                        for (FirebaseVisionBarcode barcode: barcodes) {
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();

                            int valueType = barcode.getValueType();
                            // See API reference for complete list of supported types
                            switch (valueType) {
                                case FirebaseVisionBarcode.TYPE_WIFI:
                                    String ssid = barcode.getWifi().getSsid();
                                    String password = barcode.getWifi().getPassword();
                                    int type = barcode.getWifi().getEncryptionType();
                                    break;
                                case FirebaseVisionBarcode.TYPE_URL:
                                    String title = barcode.getUrl().getTitle();
                                    String url = barcode.getUrl().getUrl();
                                    break;
                                case FirebaseVisionBarcode.TYPE_TEXT:
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRActivity.this);
//                                    builder.setMessage(barcode.getRawValue());
                                    projectID = barcode.getRawValue();
                                    toProject(projectID);
//                                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            dialogInterface.dismiss();
//                                        }
//                                    });
//                                    AlertDialog dialog = builder.create();
//                                    dialog.show();
                                    break;
                                default:
                                    break;
                            }
                        }
//                        waitingDialogue.dismiss();
                        // [END get_barcodes]
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        Toast.makeText(ScanQRActivity.this, "Firebase detect QR error", Toast.LENGTH_SHORT);
                    }
                });
    }


    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    public void toProject(final String projectID){

        firebaseFirestore.collection("projects").document(projectID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){
                                categories = documentSnapshot.getData().get("Categories").toString();
                                labels = documentSnapshot.getData().get("Labels").toString();
                                project_availability = documentSnapshot.getBoolean("Availability");
                                project_name = documentSnapshot.getData().get("Project Name").toString();

                                if(project_availability){
                                    Intent intent = new Intent(getApplicationContext(), PartiMainActivity.class);

                                    intent.putExtra("Categories",categories);
                                    intent.putExtra("Labels",labels);
                                    intent.putExtra("project_id",projectID);
                                    intent.putExtra("project_name",project_name);
                                    startActivity(intent);
                                }
                                else {
                                    showMessage("Project is no longer available!");
                                }
                            }
                            else{
                                showMessage("Project does not exists!");
                            }
                        }
                        else{
                            showMessage("Project does not exists!");
                        }
                    }
                });
    }

    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(getApplicationContext(), PartiWelcomeActivity.class));
        finish();
    }


}
