package com.jointclock.yorkapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jointclock.yorkapp.Adapter.EmergencyList;
import com.jointclock.yorkapp.Model.Emergency;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EmergencyActivity extends AppCompatActivity {

    private EmergencyList EventAdapter;
    ListView listViewEmergency;
    DatabaseReference databaseReference;
    List<Emergency> emergencys = new ArrayList<>();
    ProgressDialog pd;
    ImageView driverImg;
    EditText email,fname,lname;
    Button callPhone;
    FirebaseStorage storage;
    StorageReference storageReference;
    SearchView search,search_type;
    int oncreate = 0;
    Spinner event_type;
    List<String> eventSearch = new ArrayList<>();
    List<String> eventSearchResult = new ArrayList<>();
    List<String> eventSearch_type = new ArrayList<>();
    List<String> eventSearchResult_type = new ArrayList<>();
    List<Emergency> eventsSearchResultFinal = new ArrayList<>();
    List<Emergency> eventsSearchResultFinal_type = new ArrayList<>();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        listViewEmergency = findViewById( R.id.listViewEmergency );
        search = (SearchView) findViewById( R.id.search_emergency );
        search_type = (SearchView) findViewById( R.id.search_typeE );
        event_type = (Spinner) findViewById(R.id.spinner_typeE) ;

        databaseReference = FirebaseDatabase.getInstance().getReference("emergency");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        emergencys.clear();

        SharedPreferences prefs = getSharedPreferences("emergency", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("MyObject", "");
        Type type = new TypeToken<List<Emergency>>(){}.getType();
        emergencys = gson.fromJson(json, type);

        for (int i=0;i<emergencys.size();i++) {
            String name = emergencys.get(i).getemergencyfirstname();
            eventSearch.add(name);
            String typeTD = emergencys.get(i).getemergencytype();
            eventSearch_type.add(typeTD);
        }
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(EmergencyActivity.this,
                R.array.emergency_type_filter, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        event_type.setAdapter(adapterSpinner);

        event_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String thtype = event_type.getSelectedItem().toString();
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

        EventAdapter = new EmergencyList(EmergencyActivity.this, emergencys );
        listViewEmergency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Emergency Item = emergencys.get(i);
                ViewandEditDialog( Item.getemergencyid(), Item.getemergencyemail(), Item.getemergencyfirstname(), Item.getemergencylastname(), Item.getemergency_t_h_name(), Item.getemergencyphone(), Item.getemergencyphotoid(), Item.getemergencytype());
            }
        });
        listViewEmergency.setAdapter(EventAdapter);
        search.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
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
                EventAdapter = new EmergencyList(EmergencyActivity.this, eventsSearchResultFinal );
                listViewEmergency.setAdapter(EventAdapter);
                listViewEmergency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Emergency Item = eventsSearchResultFinal.get(position);
                        ViewandEditDialog( Item.getemergencyid(), Item.getemergencyemail(), Item.getemergencyfirstname(), Item.getemergencylastname(), Item.getemergency_t_h_name(), Item.getemergencyphone(), Item.getemergencyphotoid(), Item.getemergencytype());

                    }

                });
                return false;
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
                EventAdapter = new EmergencyList(EmergencyActivity.this, eventsSearchResultFinal_type );
                listViewEmergency.setAdapter(EventAdapter);
                listViewEmergency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Emergency Item = eventsSearchResultFinal_type.get(position);
                        ViewandEditDialog( Item.getemergencyid(), Item.getemergencyemail(), Item.getemergencyfirstname(), Item.getemergencylastname(), Item.getemergency_t_h_name(), Item.getemergencyphone(), Item.getemergencyphotoid(), Item.getemergencytype());

                    }

                });
                return false;
            }
        } );
    }


    private void ViewandEditDialog(final String emergencyid, String emgemail, String emgfname, String emglname, String emgTHname , String emgphone, final String emgphotoid, String emgtype) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(EmergencyActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.viewemg_dialog, null);
        dialogBuilder.setView(dialogView);
        fname = (EditText) dialogView.findViewById(R.id.editTextfname);
        lname = (EditText) dialogView.findViewById(R.id.editTextlname);
        final EditText job = (EditText) dialogView.findViewById(R.id.emgJob);
        final EditText THname = (EditText) dialogView.findViewById(R.id.taxi_or_hs_name);
        final TextView nameType = (TextView) dialogView.findViewById(R.id.typejob);
        email = (EditText) dialogView.findViewById(R.id.call_email);
        final EditText phone = (EditText) dialogView.findViewById(R.id.call_phone);
        driverImg = (ImageView) dialogView.findViewById( R.id.emgProfile_image ) ;
        final Spinner emgChangeSpinner = (Spinner) dialogView.findViewById( R.id.emgSpinner_type ) ;

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(EmergencyActivity.this,
                R.array.emergency_type, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        emgChangeSpinner.setAdapter(adapterSpinner);

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

        buttonEdit.setVisibility( View.GONE );
        buttonDelete.setVisibility( View.GONE );

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
    }
    private void SendEmail() {
        pd = new ProgressDialog( EmergencyActivity.this );
        pd.setMessage( "Loading..." );
        pd.show();

        final Intent i = new Intent( Intent.ACTION_SEND );
        pd.dismiss();

        i.setType( "message/rfc822" );
        i.putExtra( Intent.EXTRA_EMAIL, new String[]{email.getText().toString()} );
        i.putExtra( Intent.EXTRA_SUBJECT, "Message from " + fname.getText().toString() + " " + lname.getText().toString() );// user first and last name

        try {
            startActivity( Intent.createChooser( i, "Send mail..." ) );
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText( EmergencyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT ).show();
        }

    }
}

