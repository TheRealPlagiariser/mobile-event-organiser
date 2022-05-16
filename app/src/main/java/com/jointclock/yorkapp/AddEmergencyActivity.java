package com.jointclock.yorkapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jointclock.yorkapp.Model.Emergency;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class AddEmergencyActivity extends AppCompatActivity {

    Button addTH,emgSelectPhoto;
    EditText Emgfirstname, Emglastname,Emgphone,Emgemail,EmgTHname;
    ImageView EmgAvata;
    Spinner emgSpinner;
    String selectImgkey="0",imageString;
    private final int PICK_IMAGE_REQUEST = 10;
    private Uri filePath;
    private String downURL = "00";

    FirebaseStorage storage;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_addemergency );

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("emergency");

        emgSelectPhoto = (Button) findViewById( R.id.emgImvSelectPhoto ) ;
        addTH = (Button) findViewById( R.id.btnaddTH );
        EmgAvata = (ImageView) findViewById( R.id.emgProfile_image ) ;
        Emgfirstname = (EditText) findViewById( R.id.emgFirstName ) ;
        Emglastname = (EditText) findViewById( R.id.emgLastName ) ;
        Emgphone = (EditText) findViewById( R.id.emgPhone ) ;
        Emgemail = (EditText) findViewById( R.id.emgEmail ) ;
        EmgTHname = (EditText) findViewById( R.id.emgTHname ) ;
        emgSpinner = (Spinner) findViewById( R.id.emgSpinner_type ) ;

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this,
                R.array.emergency_type, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emgSpinner.setAdapter(adapterSpinner);

        emgSelectPhoto.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        } );

        addTH.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectImgkey.equals( "1" )){

                    addTaxiDriverorDoctor();
                }
                else{
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    addTaxiDriverorDoctor();
                }

            }
        } );
    }

    public void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        selectImgkey="1";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(AddEmergencyActivity.this.getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            EmgAvata.setImageBitmap(bitmap);
        }
    }

    private void addTaxiDriverorDoctor() {
        String fname = Emgfirstname.getText().toString().trim();
        String lname = Emglastname.getText().toString().trim();
        String phone = Emgphone.getText().toString().trim();
        String email = Emgemail.getText().toString().trim();
        String thname = EmgTHname.getText().toString().trim();
        String thtype = emgSpinner.getSelectedItem().toString();

        if (!TextUtils.isEmpty(fname)) {
            if (!TextUtils.isEmpty(lname)) {
                if (!TextUtils.isEmpty(phone)) {
                    if (!TextUtils.isEmpty(email)) {
                        if (!TextUtils.isEmpty(thname)) {

                                String id = databaseReference.push().getKey();
                                Emergency emergency = new Emergency(id, email, fname, lname,thname,phone,imageString,thtype);
                                databaseReference.child(id).setValue( emergency );
                                Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
                                finish();

                            } else {
                                Toast.makeText(this, "Please enter Taxi Number or Hospital Name.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(this, "Please enter Email", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Please enter Phone Number", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Please enter Last Name", Toast.LENGTH_LONG).show();
                }
            }
        else {
            Toast.makeText(this, "Please enter First Name", Toast.LENGTH_LONG).show();
        }
    }
}
