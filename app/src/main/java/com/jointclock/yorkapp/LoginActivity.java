package com.jointclock.yorkapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail,InputPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressBar progressBarL;
    Button Btnsignup, Btnlogin;
    CheckBox rememberinfo;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        SharedPreferences prefs = getSharedPreferences("secretkey", MODE_PRIVATE);
        String email = prefs.getString("email", "");//"No name defined" is the default value.
        String password = prefs.getString("password", "");
        int status = prefs.getInt("checked", 0);
        initializeUI();
        if (status == 1) {
            rememberinfo.setChecked(true);
        }else {
            rememberinfo.setChecked(false);
        }
        inputEmail.setText( email );
        InputPassword.setText( password );

        Btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = inputEmail.getText().toString();
                final String password=InputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address !", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password !", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBarL.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBarL.setVisibility(View.GONE);

                                if (!task.isSuccessful()) {
                                    if (password.length()< 6) {
                                        inputEmail.setVisibility(View.VISIBLE);
                                        InputPassword.setVisibility(View.VISIBLE);
                                        InputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        inputEmail.setVisibility(View.VISIBLE);
                                        InputPassword.setVisibility(View.VISIBLE);
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    if(rememberinfo.isChecked()){
                                        SharedPreferences.Editor editor = getSharedPreferences("secretkey", MODE_PRIVATE).edit();
                                        editor.putString("email", email);
                                        editor.putString("password", password);
                                        editor.putInt("checked", 1);
                                        editor.apply();
                                    }else {
                                        SharedPreferences.Editor editor = getSharedPreferences("secretkey", MODE_PRIVATE).edit();
                                        editor.putString("email", "");
                                        editor.putString("password", "");
                                        editor.putInt("checked", 0);
                                        editor.apply();
                                    }

                                    Toast.makeText(getApplicationContext(), "Log in successful !", Toast.LENGTH_LONG).show();

                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    public void initializeUI () {
        inputEmail    = findViewById(R.id.input_email_login);
        InputPassword = findViewById(R.id.input_password_login);
        progressBarL  = findViewById(R.id.progressBar_login);
        Btnsignup     = findViewById(R.id.create_one);
        Btnlogin      = findViewById(R.id.btn_login);
        rememberinfo  = findViewById(R.id.rememberinfo);
    }
}
