package com.jointclock.yorkapp.ui.emergency;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jointclock.yorkapp.Adapter.EmergencyList;
import com.jointclock.yorkapp.AddEmergencyActivity;
import com.jointclock.yorkapp.Model.Emergency;
import com.jointclock.yorkapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class EmergencyFragment extends Fragment {

    private EmergencyList EventAdapter;
    Button addEmgBtn;
    ListView listViewEmergency;
    DatabaseReference databaseReference,mRef;
    List<Emergency> emergencys = new ArrayList<>();
    ProgressDialog pd;
    String ccc="0", dddddd="0",jobType,imageString;
    private final int PICK_IMAGE_REQUEST = 10;
    ImageView driverImg;
    EditText email;
    Button callPhone;
    SearchView search,search_type;
    int oncreate = 0;
    Spinner emergency_type;

    List<String> eventSearch = new ArrayList<>();
    List<String> eventSearchResult = new ArrayList<>();
    List<String> eventSearch_type = new ArrayList<>();
    List<String> eventSearchResult_type = new ArrayList<>();
    List<Emergency> eventsSearchResultFinal = new ArrayList<>();
    List<Emergency> eventsSearchResultFinal_type = new ArrayList<>();

    private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate( R.layout.fragment_emergency, container, false );
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();

        addEmgBtn = (Button) root.findViewById( R.id.buttonAddTaxiHospital );
        listViewEmergency = root.findViewById( R.id.listViewEmergency );
        search = (SearchView) root.findViewById( R.id.search_emergency );
        search_type = (SearchView) root.findViewById( R.id.search_typeE );
        emergency_type = (Spinner) root.findViewById(R.id.spinner_typeE) ;
        databaseReference = FirebaseDatabase.getInstance().getReference("emergency");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getActivity(),
                R.array.emergency_type_filter, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emergency_type.setAdapter(adapterSpinner);

        emergency_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String thtype = emergency_type.getSelectedItem().toString();
                if (oncreate != 0) {

                    if(thtype.equals("All")){
                        search_type.setQuery("", true);
                    }else {
                        search_type.setQuery(thtype, true);
                    }
                }
                oncreate = 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                search_type.setQuery("", true);
            }
        });

        final FirebaseAuth mAuth=FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        final String userId = user.getUid();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mRef = mDatabase.child("Users").child(userId);
        DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child( "usertype" );
        Ref.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ccc = (String) dataSnapshot.getValue();
                if(ccc.equals( "1" )){
                    addEmgBtn.setVisibility(View.VISIBLE);
                }
                else {
                    addEmgBtn.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        } );

        EventAdapter = new EmergencyList(getActivity(), emergencys );
        addEmgBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oncreate = 0;
                Intent intent = new Intent( getActivity(), AddEmergencyActivity.class );
                startActivity( intent );
            }
        } );
        listViewEmergency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Emergency Item = emergencys.get(i);
                jobType = Item.getemergencytype();
                ViewandEditDialog( Item.getemergencyid(), Item.getemergencyemail(), Item.getemergencyfirstname(), Item.getemergencylastname(), Item.getemergency_t_h_name(), Item.getemergencyphone(), Item.getemergencyphotoid(), Item.getemergencytype());
            }

        });

        search.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                eventSearchResult.clear();
                eventsSearchResultFinal.clear();
                int result_num = 0;
                for (int i = 0; i < eventSearch.size();i++){
                    if (eventSearch.get(i).toUpperCase().contains(newText.toUpperCase())) {
                        eventSearchResult.add(eventSearch.get(i));
                    }
                }
                result_num = eventSearchResult.size();
                if(result_num==0){
                }
                else if(result_num==1)
                {
                    for (int i = 0;i < emergencys.size();i++){
                        if(eventSearchResult.get(0)==emergencys.get(i).getemergencyfirstname())
                        {
                            Emergency emergency = emergencys.get(i);
                            eventsSearchResultFinal.add(emergency);
                        }
                    }

                }
                else {
                    int searchresult_num = 0;
                    do {
                        for (int i = 0;i < emergencys.size();i++){
                            if(eventSearchResult.get(searchresult_num)==emergencys.get(i).getemergencyfirstname())
                            {
                                Emergency emergency = emergencys.get(i);
                                eventsSearchResultFinal.add(emergency);
                            }
                        }
                        searchresult_num++;
                    }while(searchresult_num<eventSearchResult.size());

                }
                EventAdapter = new EmergencyList(getActivity(), eventsSearchResultFinal );
                listViewEmergency.invalidateViews();

                listViewEmergency.setAdapter(EventAdapter);
                listViewEmergency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Emergency Item = eventsSearchResultFinal.get(position);
                        jobType = Item.getemergencytype();
                        ViewandEditDialog( Item.getemergencyid(), Item.getemergencyemail(), Item.getemergencyfirstname(), Item.getemergencylastname(), Item.getemergency_t_h_name(), Item.getemergencyphone(), Item.getemergencyphotoid(), Item.getemergencytype());
                    }

                });
                return true;
            }
        } );

        search_type.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                eventSearchResult_type.clear();
                eventsSearchResultFinal_type.clear();
                int result_num = 0;
                for (int i = 0; i < eventSearch_type.size();i++){
                    if (eventSearch_type.get(i).toUpperCase().contains(newText.toUpperCase())) {
                        eventSearchResult_type.add(eventSearch_type.get(i));
                    }
                }
                result_num = eventSearchResult_type.size();
                if(result_num==0){
                }
                else if(result_num==1)
                {
                    for (int i = 0;i < emergencys.size();i++){
                        if(eventSearchResult_type.get(0)==emergencys.get(i).getemergencytype())
                        {
                            Emergency Event = emergencys.get(i);
                            eventsSearchResultFinal_type.add(Event);
                        }
                    }

                }
                else {
                    int searchresult_num = 0;
                    do {
                        for (int i = 0;i < emergencys.size();i++){
                            if(eventSearchResult_type.get(searchresult_num)==emergencys.get(i).getemergencytype())
                            {
                                Emergency Event = emergencys.get(i);
                                eventsSearchResultFinal_type.add(Event);
                            }
                        }
                        searchresult_num++;
                    }while(searchresult_num<eventSearchResult_type.size());

                }
                EventAdapter = new EmergencyList(getActivity(), eventsSearchResultFinal_type );
                listViewEmergency.invalidateViews();
                listViewEmergency.setAdapter(EventAdapter);
                listViewEmergency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Emergency Item = eventsSearchResultFinal_type.get(position);
                        jobType = Item.getemergencytype();
                        ViewandEditDialog( Item.getemergencyid(), Item.getemergencyemail(), Item.getemergencyfirstname(), Item.getemergencylastname(), Item.getemergency_t_h_name(), Item.getemergencyphone(), Item.getemergencyphotoid(), Item.getemergencytype());
                    }

                });
                return true;
            }
        } );
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(mValueEventListener);
    }

    ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            emergencys.clear();
            listViewEmergency.setAdapter(null);
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                Emergency Event = postSnapshot.getValue( Emergency.class);
                String name = Event.getemergencyfirstname();
                eventSearch.add(name);
                String type = Event.getemergencytype();
                eventSearch_type.add(type);
                emergencys.add( Event );
            }
            listViewEmergency.setAdapter(EventAdapter);
            pd.dismiss();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }

    };

    private void ViewandEditDialog(final String emergencyid, String emgemail, final String emgfname, String emglname, String emgTHname , String emgphone, final String emgphotoid, String emgtype) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.viewemg_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText fname = (EditText) dialogView.findViewById(R.id.editTextfname);
        final EditText lname = (EditText) dialogView.findViewById(R.id.editTextlname);
        final EditText job = (EditText) dialogView.findViewById(R.id.emgJob);
        final EditText THname = (EditText) dialogView.findViewById(R.id.taxi_or_hs_name);
        final TextView nameType = (TextView) dialogView.findViewById(R.id.typejob);
        email = (EditText) dialogView.findViewById(R.id.call_email);
        final EditText phone = (EditText) dialogView.findViewById(R.id.call_phone);
        driverImg = (ImageView) dialogView.findViewById( R.id.emgProfile_image ) ;
        final Spinner emgChangeSpinner = (Spinner) dialogView.findViewById( R.id.emgSpinner_type ) ;

        phone.setInputType(InputType.TYPE_CLASS_PHONE);
        fname.setText(emgfname);
        lname.setText(emglname );
        email.setText(emgemail);
        phone.setText(emgphone);
        job.setText( emgtype );
        THname.setText( emgTHname );

        if(emgtype.equals( "Doctor" )){
            nameType.setText( " Hospital Name : " );
        }
        else {
            nameType.setText( " Taxi Number     : " );
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] imageBytes = baos.toByteArray();
        imageBytes = Base64.decode(emgphotoid, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        driverImg.setImageBitmap(decodedImage);

        final Button buttonEdit = (Button) dialogView.findViewById(R.id.buttonEdit);
        final Button emgSelectPhoto = (Button) dialogView.findViewById( R.id.emgViewImvSelectPhoto );
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDelete);
        final Button callEmail = (Button) dialogView.findViewById(R.id.callEmail);
        callPhone = (Button) dialogView.findViewById(R.id.callPhone);
        if(ccc.equals( "1" )){
            buttonEdit.setVisibility( View.VISIBLE );
            buttonDelete.setVisibility( View.VISIBLE );
        }
        else {
            buttonEdit.setVisibility( View.GONE );
            buttonDelete.setVisibility( View.GONE );
        }

        final AlertDialog b = dialogBuilder.create();
        b.show();

        callEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendEmail();
            }
        });

        callPhone.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri number = Uri.parse("tel:"+ phone.getText().toString());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        } );

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fname.isEnabled())
                {
                    fname.setEnabled(false);
                    lname.setEnabled(false);
                    email.setEnabled(false);
                    phone.setEnabled( false );
                    THname.setEnabled( false );
                    callEmail.setVisibility( View.VISIBLE );
                    callPhone.setVisibility( View.VISIBLE );
                    job.setEnabled( false );
                    job.setVisibility( View.VISIBLE );
                    job.setText( emgChangeSpinner.getSelectedItem().toString() );
                    emgChangeSpinner.setVisibility( View.GONE );
                    emgSelectPhoto.setVisibility(View.INVISIBLE);

                    buttonEdit.setText("Edit Profile");

                    if (dddddd.equals( "1" )){
                        updateUsers(emergencyid,email.getText().toString(),fname.getText().toString(),lname.getText().toString(),THname.getText().toString(),phone.getText().toString(),imageString,emgChangeSpinner.getSelectedItem().toString());
                        dddddd = "0";
                        getActivity().getSupportFragmentManager().beginTransaction().replace(EmergencyFragment.this.getId(), new EmergencyFragment()).commit();
                    }
                    else {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avatar);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();
                        imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                        updateUsers(emergencyid,email.getText().toString(),fname.getText().toString(),lname.getText().toString(),THname.getText().toString(),phone.getText().toString(),emgphotoid,emgChangeSpinner.getSelectedItem().toString());
                        getActivity().getSupportFragmentManager().beginTransaction().replace(EmergencyFragment.this.getId(), new EmergencyFragment()).commit();
                    }
                }
                else
                {
                    fname.setEnabled(true);
                    lname.setEnabled(true);
                    email.setEnabled(true);
                    phone.setEnabled( true );
                    THname.setEnabled( true );
                    job.setVisibility( View.INVISIBLE );
                    callEmail.setVisibility( View.GONE );
                    callPhone.setVisibility( View.GONE );
                    job.setText( "" );
                    emgChangeSpinner.setVisibility( View.VISIBLE );
                    emgSelectPhoto.setVisibility(View.VISIBLE);

                    if (jobType.equals("Taxi Driver")){
                        String [] eeee = new String[]{"Taxi Driver", "Doctor"};
                        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item, eeee);
                        emgChangeSpinner.setAdapter(adapterSpinner);
                    }else{
                        String [] anti_eeee = new String[]{"Doctor", "Taxi Driver"};
                        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item, anti_eeee);
                        emgChangeSpinner.setAdapter(adapterSpinner);
                    }

                    buttonEdit.setText("Update Profile");
                    emgSelectPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chooseImage();
                        }
                    });
                }
            }
            public void chooseImage(){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                dddddd="1";
            }
        });
        buttonDelete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setTitle("Are you sure you want to delete "+emgfname+" "+emgfname+"?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteUser(emergencyid);
                                getActivity().getSupportFragmentManager().beginTransaction().replace(EmergencyFragment.this.getId(), new EmergencyFragment()).commit();
                                b.dismiss();
                            }
                        });
                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.setIcon(android.R.drawable.ic_dialog_alert);
                alert11.show();
            }
        } );
    }

    private void SendEmail() {
        pd = new ProgressDialog( getActivity() );
        pd.setMessage( "Loading..." );
        pd.show();
        final Intent i = new Intent( Intent.ACTION_SEND );
        pd.dismiss();
        ValueEventListener eventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap hashUser = (HashMap) dataSnapshot.getValue();
                if (hashUser != null) {
                    String firstname = hashUser.get( "userfirstname" ).toString();
                    String lastname = hashUser.get( "userlastname" ).toString();
                    i.setType( "message/rfc822" );
                    i.putExtra( Intent.EXTRA_EMAIL, new String[]{email.getText().toString()} );
                    i.putExtra( Intent.EXTRA_SUBJECT, "Message from " + firstname + " " + lastname );// user first and last name
                }
                try {
                    startActivity( Intent.createChooser( i, "Send mail..." ) );
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText( getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT ).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mRef.addListenerForSingleValueEvent(eventListener);
    }

    private boolean updateUsers(String emergencyid,  String eemail,  String fname,  String lname, String thname , String phone, String photo, String type) {
        DatabaseReference UpdateReference = FirebaseDatabase.getInstance().getReference("emergency").child(emergencyid);
        Emergency Item = new Emergency( emergencyid, eemail, fname, lname, thname, phone, photo,type);
        UpdateReference.setValue( Item );
        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
        oncreate = 0;
        return true;
    }

    private boolean deleteUser(String emergencyid) {
        DatabaseReference DeleteReference = FirebaseDatabase.getInstance().getReference("emergency").child(emergencyid);
        DeleteReference.removeValue();
        Toast.makeText(getContext(), "Event Deleted", Toast.LENGTH_LONG).show();
        oncreate = 0;
        return true;
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
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            driverImg.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseReference.removeEventListener( mValueEventListener );
        oncreate = 0;
    }
}