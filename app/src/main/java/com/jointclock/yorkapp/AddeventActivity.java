package com.jointclock.yorkapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jointclock.yorkapp.Model.Event;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AddeventActivity extends AppCompatActivity implements View.OnClickListener {

    Button addBtn,camaraE;
    EditText Ename,Estarttime,Eendtime,Ediscription,Elocation,EcontactEmail,EcontactPhone,editPayment;
    ImageView Ephoto;
    Spinner spinner;
    String userId,downURL="00", dates,times,datee,timee,addCVvalue="0",addPaymentvalue="0",numberImgcheck="0",starttime, endtime, name, discription, location, contactEmail, contactPhone,  type;
    CheckBox addCVcheck,addPayment;
    private int mYear, mMonth, mDay, mHour, mMinute;
    TextView dollarMark;

    String[] typeItems = new String[]{
            "Concert",
            "Interview"
    };
    private final int PICK_IMAGE_REQUEST = 11;
    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference,databaseReference1;
    FirebaseAuth mAuth;
    List<String> values = new ArrayList<String>();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_addevent );

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("events");
        databaseReference1 = FirebaseDatabase.getInstance().getReference("item");
        mAuth=FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();
        addBtn = (Button) findViewById( R.id.buttonAddEvent );
        Ename = (EditText) findViewById( R.id.editTextname );
        Estarttime = (EditText) findViewById( R.id.editTextStartT );
        Eendtime = (EditText) findViewById( R.id.editTextEndT );
        Ediscription = (EditText) findViewById( R.id.editTextDescription );
        editPayment = (EditText) findViewById( R.id.editPayment );
        dollarMark = (TextView) findViewById( R.id.dollarMark );
        Elocation = findViewById( R.id.editTextLocation );
        EcontactEmail = findViewById( R.id.editTextContactEmail );
        EcontactPhone = findViewById( R.id.editTextPhone );
        camaraE = (Button) findViewById(R.id.imvSelectEventPhoto);
        Ephoto = (ImageView) findViewById( R.id.eventImg );
        spinner = (Spinner) findViewById(R.id.spinner_type);
        addCVcheck = (CheckBox) findViewById(R.id.addCVcheck);
        addPayment = (CheckBox) findViewById(R.id.addPayment);
        editPayment.setVisibility(View.INVISIBLE);
        dollarMark.setVisibility(View.INVISIBLE);

        addCVcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addCVcheck.isChecked()){
                    addCVvalue = "1";
                }else{
                    addCVvalue = "0";
                }
            }
        });
        addPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addPayment.isChecked()){
                    editPayment.setVisibility(View.VISIBLE);
                    dollarMark.setVisibility(View.VISIBLE);

                }else{
                    addPaymentvalue = "0";
                    editPayment.setVisibility(View.INVISIBLE);
                    editPayment.setText("0");
                    dollarMark.setVisibility(View.INVISIBLE);
                }
            }
        });

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                values.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String key = ds.getKey();
                        values.add(key);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddeventActivity.this, android.R.layout.simple_spinner_dropdown_item, values);
                    spinner.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        final List<String> ItemList = new ArrayList<>(Arrays.asList(typeItems));
//        final ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(
//                this,android.R.layout.simple_spinner_item,ItemList);
//        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapterSpinner);

        Estarttime.setOnClickListener(this);
        Eendtime.setOnClickListener(this);

        camaraE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        addBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        } );
    }

    @Override
    public void onClick(View v) {

        if (v == Estarttime) {
            hideSoftKeyboard(Ename);
            hideSoftKeyboard(Eendtime);
            hideSoftKeyboard(Estarttime);
            hideSoftKeyboard(Ediscription);
            hideSoftKeyboard(Elocation);
            hideSoftKeyboard(EcontactEmail);
            hideSoftKeyboard(EcontactPhone);
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        String hourofDayString = "";
                        String minuteofString = "";

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            if (String.valueOf(hourOfDay).length() == 1 ) {
                                hourofDayString = "0" + hourOfDay;
                            }else {
                                hourofDayString = String.valueOf(hourOfDay);
                            }

                            if (String.valueOf(minute).length() == 1) {
                                minuteofString = "0" + minute;
                            }else {
                                minuteofString = String.valueOf(minute);
                            }

                            times = hourofDayString + ":" + minuteofString;
                            Estarttime.setText( dates+" "+times );
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        String monthofYearString = "", dayofYearString = "";

                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            if(String.valueOf(monthOfYear + 1).length() == 1) {
                                monthofYearString = "0" + (monthOfYear + 1);
                            }else  {
                                monthofYearString = String.valueOf(monthOfYear + 1);
                            }

                            if (String.valueOf(dayOfMonth).length() == 1) {
                                dayofYearString = "0" + dayOfMonth;
                            }else {
                                dayofYearString = String.valueOf(dayOfMonth);
                            }


                            dates = year + "-" + monthofYearString + "-" +dayofYearString ;

                        }
                    }, mYear, mMonth, mDay);

            datePickerDialog.show();
        }
        if (v == Eendtime) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        String hourofDayString = "";
                        String minuteofString = "";
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            if (String.valueOf(hourOfDay).length() == 1 ) {
                                hourofDayString = "0" + hourOfDay;
                            }else {
                                hourofDayString = String.valueOf(hourOfDay);
                            }
                            if (String.valueOf(minute).length() == 1) {
                                minuteofString = "0" + minute;
                            }else {
                                minuteofString = String.valueOf(minute);
                            }
                            timee = hourofDayString + ":" + minuteofString;
                            Eendtime.setText( datee+"-"+timee );
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        String monthofYearString = "", dayofYearString = "";
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            if(String.valueOf(monthOfYear + 1).length() == 1) {
                                monthofYearString = "0" + (monthOfYear + 1);
                            }else  {
                                monthofYearString = String.valueOf(monthOfYear + 1);
                            }

                            if (String.valueOf(dayOfMonth).length() == 1) {
                                dayofYearString = "0" + dayOfMonth;
                            }else {
                                dayofYearString = String.valueOf(dayOfMonth);
                            }
                            datee = year + "-" + monthofYearString + "-" +dayofYearString ;
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }
    private void addEvent() {

        name = Ename.getText().toString().trim();
        starttime = Estarttime.getText().toString().trim();
        endtime = Eendtime.getText().toString().trim();
        discription = Ediscription.getText().toString().trim();
        location = Elocation.getText().toString().trim();
        contactEmail = EcontactEmail.getText().toString().trim();
        contactPhone = EcontactPhone.getText().toString().trim();
        type = spinner.getSelectedItem().toString();
        addPaymentvalue = editPayment.getText().toString();

        if (!TextUtils.isEmpty(name)) {
            if (!TextUtils.isEmpty(starttime)) {
                if (!TextUtils.isEmpty(endtime)) {
                    if (!TextUtils.isEmpty(discription)) {
                        if (!TextUtils.isEmpty(location)) {
                            if (!TextUtils.isEmpty(contactEmail)) {
                                if (!TextUtils.isEmpty(contactPhone)) {
                                        if(numberImgcheck.equals("1")){
                                            uploadImage();
                                        }
                                        else {
                                            String id = databaseReference.push().getKey();
                                            Event Item = new Event(id, starttime, endtime, name, discription, location, contactEmail, contactPhone, downURL, type, addCVvalue,userId,addPaymentvalue);
                                            databaseReference.child(id).setValue(Item);
                                            Toast.makeText(this, "Event added", Toast.LENGTH_LONG).show();
                                            DatabaseReference eventAttend = databaseReference.child(id).child("eventattendusers");
                                            DateFormat df = new SimpleDateFormat("yyyy MM dd, HH:mm:ss");
                                            String date = df.format(Calendar.getInstance().getTime());
                                            String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            eventAttend.child(myid).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    finish();
                                                }
                                            });
                                        }
                                    } else {
                                        Toast.makeText(this, "Please enter Your Phone Number.", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(this, "Please enter Your Email.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(this, "Please enter Location", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(this, "Please enter Description", Toast.LENGTH_LONG).show();
                        }
                } else {
                    Toast.makeText(this, "Please enter End time", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Please enter Start time", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please enter Name", Toast.LENGTH_LONG).show();
        }
    }
    public void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        numberImgcheck="1";
    }

    public void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(AddeventActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(AddeventActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            Ephoto.setImageResource(0);
                            //TODO; download URL getting
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downURL = uri.toString();
                                    String id = databaseReference.push().getKey();
                                    Event Item = new Event(id, starttime, endtime, name, discription, location, contactEmail, contactPhone, downURL, type, addCVvalue,userId,addPaymentvalue);
                                    databaseReference.child(id).setValue(Item);
                                    Toast.makeText(AddeventActivity.this, "Event added", Toast.LENGTH_LONG).show();
                                    DatabaseReference eventAttend = databaseReference.child(id).child("eventattendusers");
                                    DateFormat df = new SimpleDateFormat("yyyy MM dd, HH:mm:ss");
                                    String date = df.format(Calendar.getInstance().getTime());
                                    String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    eventAttend.child(myid).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            progressDialog.dismiss();
                                            finish();
                                        }
                                    });
                                    numberImgcheck="0";
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddeventActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService( Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
                Ephoto.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
