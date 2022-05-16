package com.jointclock.yorkapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jointclock.yorkapp.Model.User;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailTV,phoneTV,passwordTV,nameTVF,nameTVL,repasswordTV;
    private Button regBtn,loginbtn;
    private ProgressBar progressBarS;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    CheckBox rememberinfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        loginbtn=(Button) findViewById(R.id.btn_login1);

        mAuth=FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        initializeUI();
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerNewUser() {
        progressBarS.setVisibility(View.VISIBLE);
        final String email,Pnumber, password, repassword, nameF,nameL;
        email=emailTV.getText().toString();
        Pnumber=phoneTV.getText().toString();
        password=passwordTV.getText().toString();
        nameF=nameTVF.getText().toString();
        nameL=nameTVL.getText().toString();
        repassword=repasswordTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email.", Toast.LENGTH_LONG).show();
            progressBarS.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(Pnumber)) {
            Toast.makeText(getApplicationContext(), "Please enter phone number.", Toast.LENGTH_LONG).show();
            progressBarS.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password.", Toast.LENGTH_LONG).show();
            progressBarS.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(nameF)) {
            Toast.makeText(getApplicationContext(), "Please enter your first name.", Toast.LENGTH_LONG).show();
            progressBarS.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(nameL)) {
            Toast.makeText(getApplicationContext(), "Please enter your last name.", Toast.LENGTH_LONG).show();
            progressBarS.setVisibility(View.GONE);
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            progressBarS.setVisibility(View.GONE);
            return;
        }
        if (!password.equals(repassword)) {
            Toast.makeText(getApplicationContext(), "Password does not match.", Toast.LENGTH_LONG).show();
            progressBarS.setVisibility(View.GONE);
            return;
        }
        mAuth.createUserWithEmailAndPassword( email, password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                if(rememberinfo.isChecked()){
                                    SharedPreferences.Editor editor = getSharedPreferences("secretkey", MODE_PRIVATE).edit();
                                    editor.putString("email", email);
                                    editor.putString("password", password);
                                    editor.putInt("checked", 1);
                                    editor.apply();
                                }
                                emailTV.setText("");
                                phoneTV.setText("");
                                passwordTV.setText("");
                                nameTVF.setText("");
                                nameTVL.setText("");
                                repasswordTV.setText( "" );
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                DatabaseReference mRef = mDatabase.child( "Users" ).child( userID );
                                User mUser = new User(userID, email, nameF, nameL, Pnumber, password, "00", "0");
                                mRef.setValue( mUser ).addOnCompleteListener( new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressBarS.setVisibility( View.GONE );
                                            Toast.makeText( SignUpActivity.this, "Sign Up Successful!", Toast.LENGTH_LONG ).show();
                                            enteradminpw();
                                        }
                                    }
                                } );
                            }
                            else {
                                Toast.makeText( SignUpActivity.this, "Error!", Toast.LENGTH_LONG ).show();
                            }
                        }
                    } );
    }

    private void initializeUI () {
        emailTV=findViewById(R.id.email);
        phoneTV=findViewById(R.id.phonenumber);
        nameTVF=findViewById(R.id.edtFirstname);
        nameTVL=findViewById(R.id.edtLastname);
        passwordTV=findViewById(R.id.password);
        repasswordTV=findViewById(R.id.input_reEnterPassword);
        regBtn=findViewById(R.id.signupBtn);
        progressBarS=findViewById(R.id.progressBar_sign);
        rememberinfo = findViewById(R.id.rememberinfo_signup);
    }
    public void enteradminpw() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(SignUpActivity.this);
        edittext.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setTitle("Enter admin key."); // enter admin key
        alert.setView(edittext);
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        final DatabaseReference mRef = mDatabase.child("Users").child(userId);
        final DatabaseReference aaRef = mDatabase.child("adminkey");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String enteredAdminKey = edittext.getText().toString();
                aaRef.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String aaa = dataSnapshot.getValue().toString();
                        if (enteredAdminKey.equals(aaa)) {
                            // you are admin
                            mRef.child("usertype").setValue("1");
                            Toast.makeText(SignUpActivity.this, "Password is correct, You are admin.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(SignUpActivity.this, "Password is incorrect, Please try again.", Toast.LENGTH_SHORT).show();
                            enteradminpw();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                mRef.child("usertype").setValue("0");

                Toast.makeText(SignUpActivity.this, "You are general user.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                finish();
            }
        });
        alert.show();
    }
}

