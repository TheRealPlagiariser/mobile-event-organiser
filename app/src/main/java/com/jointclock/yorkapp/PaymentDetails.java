package com.jointclock.yorkapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PaymentDetails extends AppCompatActivity {
    TextView txtId,txtAmount,txtStatus;
    DatabaseReference databaseReference,mrefhoster;
    String EID,EHosterID,fileandpaymentValue, downURL_file    ,filePathdoc,eventId ,Attached_fileName;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        databaseReference = FirebaseDatabase.getInstance().getReference("events");
        mrefhoster = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        txtId = findViewById(R.id.txtId);
        txtAmount = findViewById(R.id.txtAmount);
        txtStatus = findViewById(R.id.txtStatus);

        Intent intent = getIntent();
        String paymentAmount = intent.getStringExtra("Amount");
        EID = intent.getStringExtra("EventID");
        EHosterID = intent.getStringExtra("EventhosterID");
        fileandpaymentValue = intent.getStringExtra("fileandpaymentValue");

        filePathdoc = intent.getStringExtra("filePathdoc");
        eventId = intent.getStringExtra("eventId");
        Attached_fileName = intent.getStringExtra("Attached_fileName");

        txtAmount.setText("$"+paymentAmount);
        try {
            JSONObject jsonObject = new JSONObject();
            showDetails(jsonObject.getJSONObject("response"),intent.getStringExtra("Amount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(PaymentDetails.this, "$"+paymentAmount+" was paid successfully.", Toast.LENGTH_SHORT).show();

        DateFormat df = new SimpleDateFormat("yyyy MM dd, HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());
        DatabaseReference eventAttend = databaseReference.child(EID).child("eventattendusers");
        final DatabaseReference hosterid = mrefhoster.child(EHosterID).child("userattendevents");
        final String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        eventAttend.child(myid).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                hosterid.child(myid).setValue(EID).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(fileandpaymentValue.equals("1")){
                            uploadFile();
                        }else{
                            Toast.makeText(PaymentDetails.this, "Successfully attended", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            }
        });
    }

    private void showDetails(JSONObject response, String paymentAmount) {
            txtId.setText("nn");
            txtStatus.setText("kk");
            txtAmount.setText("$"+paymentAmount);
    }

    public void uploadFile() {

        if(filePathdoc != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(PaymentDetails.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            Date currentTime = Calendar.getInstance().getTime();
            final StorageReference ref = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentTime.toString()).child(Attached_fileName);
            ref.putFile(Uri.parse(filePathdoc))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(PaymentDetails.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            //TODO; download URL getting
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downURL_file = uri.toString();
                                    final DatabaseReference eventAttend = databaseReference.child(eventId).child("eventattendusers");
                                    final String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    String filepath = filePathdoc.toString();
                                    String fileurl = downURL_file;
                                    List<String> info = new ArrayList<String>();
                                    info.add(myid);
                                    info.add(filepath);
                                    info.add(fileurl);
                                    eventAttend.child(myid).setValue(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(PaymentDetails.this, "you Attended", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(PaymentDetails.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

}