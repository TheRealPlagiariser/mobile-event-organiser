package com.jointclock.yorkapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jointclock.yorkapp.Adapter.UserList;
import com.jointclock.yorkapp.Model.User;

import java.util.ArrayList;
import java.util.List;

public class ViewUsersActivity extends AppCompatActivity {
    ProgressDialog pd;
    List<User> Users;
    ListView listViewUsers;
    DatabaseReference mDatabaseReference;
    DatabaseReference rootRef;
    List<String> userlist = new ArrayList<String>();
    String eventID,cvable;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_viewusers );

        rootRef = FirebaseDatabase.getInstance().getReference("Users");
        listViewUsers = (ListView) findViewById(R.id.bookusers);
        Users = new ArrayList<>();
        pd = new ProgressDialog(ViewUsersActivity.this);
        pd.setMessage("Loading...");
        pd.show();
        Bundle extra = getIntent().getExtras();
        eventID = extra.getString("eventid");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("events").child(eventID);
        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                User Item = Users.get(i);
                String selecteduserid = Item.getuserid();
                final String usertype = Item.getusertype();
                final DatabaseReference fileRef = mDatabaseReference.child("eventattendusers").child(selecteduserid).child(String.valueOf(2));
                DatabaseReference eventcv = mDatabaseReference.child("eventaddcv");
                eventcv.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        cvable = (String) dataSnapshot.getValue();
                        if(usertype.equals("0") && cvable.equals("1")){
                            fileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String url = (String) dataSnapshot.getValue();
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(url));
                                    startActivity(intent);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else {
                            Toast.makeText(getApplication(), "none.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        rootRef.addValueEventListener(mValueEventListener);
    }

    ValueEventListener mValueEventListener =new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            Users.clear();
            Bundle extra = getIntent().getExtras();
            userlist = extra.getStringArrayList("userid");
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
            {
                User User = postSnapshot.getValue(User.class );
                 for(int i = 0; i< userlist.size(); i++){
                     if(userlist.get(i).equals(User.getuserid())){
                         Users.add( User );
                     }
                 }
            }
            pd.dismiss();
            UserList UserAdapter = new UserList(ViewUsersActivity.this, Users );
            listViewUsers.setAdapter(UserAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        rootRef.removeEventListener( mValueEventListener );
    }
}


