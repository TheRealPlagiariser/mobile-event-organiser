package com.jointclock.yorkapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PaypalActivity extends AppCompatActivity {
    private static final int PAYPAL_REQUEST_CODE = 7777;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    Button btnPayNow;
    EditText edtAmount;
    TextView detailtxt;
    String paymentValue,eventhosterID,eventID,fileandpaymentValue,filePathdoc,eventId,Attached_fileName,downURL_file;

    DatabaseReference databaseReference,mrefhoster;
    StorageReference storageReference;

    @Override
    protected void onDestroy() {
        stopService(new Intent(this,PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);



        Intent intent = new Intent(this,PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        databaseReference = FirebaseDatabase.getInstance().getReference("events");
        mrefhoster = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        btnPayNow = findViewById(R.id.btnPayNow);
        edtAmount = findViewById(R.id.edtAmount);
        detailtxt = findViewById(R.id.detailtxt);
        Bundle extra = getIntent().getExtras();
        paymentValue = extra.getString("paymentValue");
        eventID = extra.getString("eventID");
        eventhosterID = extra.getString("eventhosterID");
        fileandpaymentValue = extra.getString("fileandpaymentValue");

        filePathdoc = extra.getString("filePathdoc");
        eventId = extra.getString("eventId");
        Attached_fileName = extra.getString("Attached_fileName");

        edtAmount.setText(paymentValue);
        detailtxt.setText("To attend, you must pay "+paymentValue+"$ .");
        processPayment();
    }

    private void processPayment() {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(paymentValue)),"USD",
                "To Attend...",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent,PAYPAL_REQUEST_CODE);
    }
    public class Config {
        public static final String PAYPAL_CLIENT_ID = "AbU8Xcyjo2sNOzFEP71qvmBDhBoEngCR2KSY-txWw5PPthIBwsxCYQS1QTnP7fpUeZky_YJygBXrvycd";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    DateFormat df = new SimpleDateFormat("yyyy MM dd, HH:mm:ss");
                    String date = df.format(Calendar.getInstance().getTime());
                    DatabaseReference eventAttend = databaseReference.child(eventId).child("eventattendusers");
                    final DatabaseReference hosterid = mrefhoster.child(eventhosterID).child("userattendevents");
                    final String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    eventAttend.child(myid).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hosterid.child(myid).setValue(eventId).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if(fileandpaymentValue.equals("1")){
                                        uploadFile();
                                    }else{
                                        Toast.makeText(PaypalActivity.this, "Successfully attended", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }
                            });
                        }
                    });
                }
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
    }
    public void uploadFile() {

        if(filePathdoc != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(PaypalActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            Date currentTime = Calendar.getInstance().getTime();
            final StorageReference ref = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentTime.toString()).child(Attached_fileName);
            ref.putFile(Uri.parse(filePathdoc))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(PaypalActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            //TODO; download URL getting
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downURL_file = uri.toString();
                                    final DatabaseReference eventAttend = databaseReference.child(eventId).child("eventattendusers");
                                    final String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    String filepath = filePathdoc;
                                    String fileurl = downURL_file;
                                    List<String> info = new ArrayList<String>();
                                    info.add(myid);
                                    info.add(filepath);
                                    info.add(fileurl);
                                    eventAttend.child(myid).setValue(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(PaypalActivity.this, "you Attended", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(PaypalActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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