package com.jointclock.yorkapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jointclock.yorkapp.Model.Emergency;

import java.util.ArrayList;
import java.util.List;

public class SelectActivity extends Activity {
    ImageButton appBtn, authBtn;
    private boolean exit = false;
    FirebaseAuth auth;
    DatabaseReference emergency;
    List<Emergency> emlist = new ArrayList<>();
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        pd = new ProgressDialog(SelectActivity.this);
        pd.setMessage("Loading...");
        pd.show();

        auth = FirebaseAuth.getInstance();
        emergency = FirebaseDatabase.getInstance().getReference().child("emergency");
        emergency.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String emergencyID = ds.getKey();
                        List<String> emusers = new ArrayList<String>();
                        emusers.add(emergencyID);
                        Emergency emergency = ds.getValue(Emergency.class);
                        emlist.add(emergency);
                        SharedPreferences.Editor prefsEditor = getSharedPreferences("emergency", MODE_PRIVATE).edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(emlist);
                        prefsEditor.putString("MyObject", json);
                        prefsEditor.commit();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        pd.dismiss();
        appBtn  = (ImageButton) findViewById( R.id.btn_app );
        authBtn = (ImageButton) findViewById( R.id.btn_auth );
        appBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
                Intent intent = new Intent(SelectActivity.this, EmergencyActivity.class);
                startActivity(intent);
            }
        } );

        authBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
                Intent intent = new Intent(SelectActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        } );
    }

    @Override
    public void onBackPressed() {

        if (exit) {
            finish();
        } else {
            Toast.makeText(this, this.getString(R.string.press_back_again_to_exit), Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed( new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3000);
        }
    }


}