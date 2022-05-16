package com.jointclock.yorkapp.ui.profile;



import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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
import com.jointclock.yorkapp.MainActivity;
import com.jointclock.yorkapp.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    ImageView profileImg;
    ProgressDialog pd;
    private final int PICK_IMAGE_REQUEST = 10;
    private Uri filePath;
    FirebaseStorage storage;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    List<String> items = new ArrayList<String>();

    ListView listView;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate( R.layout.fragment_profile, container, false);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("item");

        final TextView textEmailV = root.findViewById(R.id.edtEmail);
        final TextView textfirNV = root.findViewById(R.id.edtFirstName);
        final TextView textlastNV = root.findViewById(R.id.edtLastName);
        final TextView textphoneNV = root.findViewById(R.id.edtPhone);
        final TextView textresN = root.findViewById(R.id.employer);
        final Button profileButton = root.findViewById(R.id.btnUpdateUserprofile);
        final Button camaraI = root.findViewById(R.id.imvSelectPhoto);
        final Button resetBtn = root.findViewById( R.id.resetAdminkey );
        final Button addBtn = root.findViewById(R.id.addkindBtn);
        listView = root.findViewById(R.id.listkind);
        profileImg = root.findViewById(R.id.profile_image);

        final FirebaseAuth mAuth=FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mRef = mDatabase.child("Users").child(userId);
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap hashUser = (HashMap) dataSnapshot.getValue();
                if (hashUser != null) {
                    final String emailAddress = hashUser.get("useremail").toString();
                    final String firstname = hashUser.get("userfirstname").toString();
                    final String lastname = hashUser.get("userlastname").toString();
                    final String phoneN = hashUser.get("userphone").toString();
                    final String resNum = hashUser.get("usertype").toString();
                    final String photoN = hashUser.get( "userphotoid" ).toString();

                    textEmailV.setText(emailAddress);
                    textfirNV.setText(firstname);
                    textlastNV.setText(lastname);
                    textphoneNV.setText(phoneN);
                    if(resNum.equals( "1" )){
                        textresN.setText( "You are Event Hoster. (Admin)" );
                    }else{
                        textresN.setText( "You are Client. (User)" );
                        resetBtn.setVisibility( View.GONE );
                        addBtn.setVisibility( View.GONE );
                        listView.setVisibility( View.GONE );

                    }

                    if(photoN.equals( "00" )){
                        Picasso.get().load(R.drawable.avatar).into(profileImg);
                    }
                    else {
                        Picasso.get().load(photoN).into(profileImg);
                    }
                    pd.dismiss();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage() , Toast.LENGTH_SHORT).show();

            }
        });
        textfirNV.setEnabled(false);
        textlastNV.setEnabled(false);
        textphoneNV.setEnabled(false);

        camaraI.setVisibility(View.INVISIBLE);

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textfirNV.isEnabled())
                {
                    textfirNV.setEnabled(false);
                    textlastNV.setEnabled(false);
                    textphoneNV.setEnabled(false);
                    camaraI.setVisibility(View.INVISIBLE);

                    profileButton.setText("Edit Profile");

                    mRef.child("userfirstname").setValue(textfirNV.getText().toString());
                    mRef.child("userlastname").setValue(textlastNV.getText().toString());
                    mRef.child("useremail").setValue(textEmailV.getText().toString());
                    mRef.child("userphone").setValue(textphoneNV.getText().toString());

                }
                else
                {
                    textfirNV.setEnabled(true);
                    textlastNV.setEnabled(true);
                    textphoneNV.setEnabled(true);
                    camaraI.setVisibility(View.VISIBLE);

                    profileButton.setText("Update Profile");

                    camaraI.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chooseImage();
                        }
                    });
                }
                uploadImage();
            }

            public void chooseImage(){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
            public void uploadImage() {
                if(filePath != null)
                {
                    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();
                    final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
                    ref.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                                    //TODO; download URL getting
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final String downURL = uri.toString();
                                            mRef.child( "userphotoid" ).setValue( downURL);
                                            Intent intent = new Intent( getActivity(), MainActivity.class );
                                            startActivity( intent );
                                            getActivity().finish();
                                        }
                                    });
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        resetBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAdminKey();
            }
        } );

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                final EditText edittext = new EditText( getActivity());
                alert.setTitle("Add");
                alert.setView(edittext);
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String item = edittext.getText().toString();
                        databaseReference.child(item).setValue("");
                        Toast.makeText(getActivity(), "added.", Toast.LENGTH_SHORT).show();

                    }

                });

                alert.setNegativeButton(
                        "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                alert.show();
            }
        });

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_1, android.R.id.text1, values);
//        listView.setAdapter(adapter);
        databaseReference.addValueEventListener(mValueEventListener);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String name =(listView.getItemAtPosition(position).toString());
                databaseReference.child(name).removeValue();
                Toast.makeText(getActivity(), "deleted.", Toast.LENGTH_SHORT).show();

                return false;
            }
        });

        return root;
    }

    ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            items.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    items.add(key);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, items);
                listView.setAdapter(adapter);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                profileImg.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void resetAdminKey() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final EditText edittext = new EditText( getActivity());
        edittext.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setTitle("Enter Current Admin Key."); // enter admin key
        alert.setView(edittext);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String enteredAdminKey = edittext.getText().toString();
                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                final DatabaseReference aaRef = mDatabase.child( "adminkey" );
                aaRef.addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String aaa = dataSnapshot.getValue().toString();
                        if (enteredAdminKey.equals(aaa) ){

                            AlertDialog.Builder alertReset = new AlertDialog.Builder(getActivity());
                            final EditText edittext1 = new EditText( getActivity());
                            edittext1.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            edittext1.setHint( " Admin Key " );
                            final EditText edittext2 = new EditText( getActivity());
                            edittext2.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            edittext2.setHint( " Confirm Admin Key " );
                            LinearLayout ll=new LinearLayout(getActivity());
                            ll.setOrientation( LinearLayout.VERTICAL);
                            ll.addView(edittext1);
                            ll.addView(edittext2);
                            alertReset.setView(ll);

                            alertReset.setCancelable(false);
                            alertReset.setTitle("Update Admin Key."); // enter admin key
                            alertReset.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    final String enterednewAdminKey = edittext1.getText().toString();
                                    final String reenterednewAdminKey = edittext2.getText().toString();
                                    if (TextUtils.isEmpty(enterednewAdminKey)) {
                                        Toast.makeText(getActivity(), "Please fill in the blanks.", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    if (!enterednewAdminKey.equals(reenterednewAdminKey)) {
                                        Toast.makeText(getActivity(), "No matched password.", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    aaRef.setValue( enterednewAdminKey );
                                    Toast.makeText(getActivity(), "Changed Admin Key Successful.", Toast.LENGTH_SHORT).show();


                                }

                            });
                            alertReset.setNegativeButton(
                                    "Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert11 = alertReset.create();
                            alert11.setIcon(android.R.drawable.ic_dialog_alert);
                            alert11.show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                } );
            }

        });

        alert.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseReference.removeEventListener( mValueEventListener );
        items.clear();
    }

}