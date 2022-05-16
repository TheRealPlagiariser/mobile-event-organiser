package com.jointclock.yorkapp.ui.home;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.jointclock.yorkapp.Adapter.EventList;
import com.jointclock.yorkapp.AddeventActivity;
import com.jointclock.yorkapp.AlarmReceiver;
import com.jointclock.yorkapp.Model.Event;
import com.jointclock.yorkapp.PaypalActivity;
import com.jointclock.yorkapp.R;
import com.jointclock.yorkapp.ViewUsersActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ALARM_SERVICE;

public class HomeFragment extends Fragment {

    DatabaseReference databaseReference,mrefhoster,databaseReference1;
    ListView listViewEvents;
    List<Event> events = new ArrayList<>();
    List<Event> eventsById = new ArrayList<>();
    Event eventsForOrder;
    List<Event> eventsSearchResultFinal = new ArrayList<>();
    List<Event> eventsSearchResultFinal_type = new ArrayList<>();
    Button addBtn;
    SearchView search,search_type;
    Spinner event_type;
    Button buttonAttend;
    ProgressDialog pd;
    List<String> users = new ArrayList<String>();
    List<String> eventSearch = new ArrayList<>();
    List<String> eventSearchResult = new ArrayList<>();
    List<String> eventSearch_type = new ArrayList<>();
    List<String> eventSearchResult_type = new ArrayList<>();

    String number = "0",addCVvalue="0",eventType,eventAddcv,userId,addPaymentvalue="0",attendedValue = "0",fileandpaymentValue = "0";
    private final int PICK_IMAGE_REQUEST = 10;
    private final int PICK_FILE_REQUEST = 11;
    private int notificationId = 1;
    private Uri filePath, filePathdoc;
    FirebaseStorage storage;
    StorageReference storageReference;

    int oncreate = 0;
    int year = 0, month = 0, day = 0;
    private EventList EventAdapter;
    String bbb, downURL="00",downURL_file,Estime, Eetime, Ename, Edescription, Elocation, Eemail, Ephone, Etype, eventId,aaaaaa="0";
    String  dates,times,datee,timee;
    private int mYear, mMonth, mDay, mHour, mMinute;
    ImageView eventImg;
    TextView attached_fileName,add_payment_detail;
    AlertDialog b;

    List<String> values = new ArrayList<String>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate( R.layout.fragment_home, container, false );
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("events");
        databaseReference1 = FirebaseDatabase.getInstance().getReference("item");
        mrefhoster = FirebaseDatabase.getInstance().getReference("Users");
        listViewEvents = (ListView) root.findViewById( R.id.listViewEvents );
        addBtn  = (Button) root.findViewById( R.id.buttonAddEvent );
        search = (SearchView) root.findViewById( R.id.search );
        search_type = (SearchView) root.findViewById( R.id.search_type );
        event_type = (Spinner) root.findViewById(R.id.spinner_type) ;

//        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(getActivity(),
//                values, android.R.layout.simple_spinner_item);
//        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        event_type.setAdapter(adapterSpinner);
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                values.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String key = ds.getKey();
                        values.add(key);
                    }
//                    String[] items = new String[]{ "All", values.toString()};
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, values);
                    event_type.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();

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

        final FirebaseAuth mAuth=FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child( "usertype" );
        mRef.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bbb = (String) dataSnapshot.getValue();
                if (bbb.equals( "1" )){
                    addBtn.setVisibility( View.VISIBLE );
                }else {
                    addBtn.setVisibility( View.INVISIBLE );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
        addBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                startActivity(getActivity().getIntent());
                Intent intent = new Intent( getActivity(), AddeventActivity.class );
                startActivity( intent );
            }
        } );
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
                    for (int i = 0;i < eventsById.size();i++){
                        if(eventSearchResult.get(0) == eventsById.get(i).geteventname())
                        {
                            Event Event = eventsById.get(i);
                            eventsSearchResultFinal.add(Event);
                        }
                    }
                }
                else {
                    int searchresult_num = 0;
                    do {
                        for (int i = 0;i < eventsById.size();i++){
                            if(eventSearchResult.get(searchresult_num) == eventsById.get(i).geteventname())
                            {
                                Event Event = eventsById.get(i);
                                eventsSearchResultFinal.add(Event);
                            }
                        }
                        searchresult_num++;
                    }while(searchresult_num<eventSearchResult.size());

                }
                EventAdapter = new EventList(getActivity(), eventsSearchResultFinal );
                listViewEvents.clearTextFilter();
                listViewEvents.setAdapter(EventAdapter);
                listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                        if (bbb.equals( "1" )){
                            Event Item = eventsSearchResultFinal.get(i);
                            eventType = Item.geteventtype();
                            eventAddcv = Item.geteventaddcv();
                            CallUpdateAndDeleteDialog( Item.geteventid(), Item.geteventdatetimestart(), Item.geteventdatetimeend(), Item.geteventname(), Item.geteventdescription(), Item.geteventlocation(), Item.geteventcontactemail(), Item.getEventcontactphone(), Item.geteventphotoid(), Item.geteventtype(),Item.geteventaddcv(),Item.geteventpaymentvalue());
                        }else {
                            Event Item = eventsSearchResultFinal.get(i);
                            EventViewDialog( Item.geteventid(), Item.geteventdatetimestart(), Item.geteventdatetimeend(), Item.geteventname(), Item.geteventdescription(), Item.geteventlocation(), Item.geteventcontactemail(), Item.getEventcontactphone(), Item.geteventphotoid(), Item.geteventtype(),Item.geteventaddcv(),Item.geteventhosterid(),Item.geteventpaymentvalue());
                        }
                    }
                });
                return false;
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
        search_type_func();

    }

    private void search_type_func() {
        search_type.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                eventSearchResult_type.clear();
                eventsSearchResultFinal_type.clear();
                int result_num = 0;
                for (int i = 0; i < eventSearch_type.size();i++){
                    if (eventSearch_type.get(i).equals(newText)) {
                        eventSearchResult_type.add(eventSearch_type.get(i));
                    }
                }
                result_num = eventSearchResult_type.size();
                if(result_num == 0) {
                    for (int i = 0; i < eventsById.size(); i ++) {
                        eventsSearchResultFinal_type.add(eventsById.get(i));
                    }
                    EventAdapter = new EventList(getActivity(), eventsSearchResultFinal_type);
                } else {
                    for (int i = 0; i < eventsById.size(); i ++) {
                        if (eventsById.get(i).geteventtype().equals(newText)) {
                            eventsSearchResultFinal_type.add(eventsById.get(i));
                        }
                    }
                    EventAdapter = new EventList(getActivity(), eventsSearchResultFinal_type);
                }
                listViewEvents.clearTextFilter();
                listViewEvents.setAdapter(EventAdapter);
                listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                        if (bbb.equals( "1" )){
                            Event Item = eventsSearchResultFinal_type.get(i);
                            eventType = Item.geteventtype();
                            eventAddcv = Item.geteventaddcv();
                            CallUpdateAndDeleteDialog( Item.geteventid(), Item.geteventdatetimestart(), Item.geteventdatetimeend(), Item.geteventname(), Item.geteventdescription(), Item.geteventlocation(), Item.geteventcontactemail(), Item.getEventcontactphone(), Item.geteventphotoid(), Item.geteventtype(),Item.geteventaddcv(),Item.geteventpaymentvalue());
                        }else {
                            Event Item = eventsSearchResultFinal_type.get(i);
                            EventViewDialog( Item.geteventid(), Item.geteventdatetimestart(), Item.geteventdatetimeend(), Item.geteventname(), Item.geteventdescription(), Item.geteventlocation(), Item.geteventcontactemail(), Item.getEventcontactphone(), Item.geteventphotoid(), Item.geteventtype(),Item.geteventaddcv(),Item.geteventhosterid(),Item.geteventpaymentvalue());
                        }
                    }
                });
                return false;
            }
        } );
    }

    ValueEventListener mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                events.clear();
                eventsById.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Event Event = postSnapshot.getValue( Event.class);
                    String name = Event.geteventname();
                    String id_of_event = Event.geteventhosterid();
                    if (userId.equals(id_of_event)) {
                        eventsById.add(Event);
                    } else {
                        events.add( Event );
                    }
                    eventSearch.add(name);
                    String type = Event.geteventtype();
                    eventSearch_type.add(type);
                }
                Collections.sort(eventsById, new Comparator<Event>() {

                    @Override
                    public int compare(Event o1, Event o2) {
                        return o1.geteventdatetimestart().compareTo(o2.geteventdatetimestart());
                    }
                });
                Collections.sort(events, new Comparator<Event>() {

                    @Override
                    public int compare(Event o1, Event o2) {
                        return o1.geteventdatetimestart().compareTo(o2.geteventdatetimestart());
                    }
                });
                for (int i = 0; i < events.size(); i ++) {
                    eventsForOrder = new Event();
                    eventsForOrder = events.get(i);
                    eventsById.add(eventsForOrder);
                }
                EventAdapter = new EventList(getActivity(), eventsById );
                listViewEvents.setAdapter(EventAdapter);
                listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                        if (bbb.equals( "1" )){
                            Event Item = eventsById.get(i);
                            eventType = Item.geteventtype();
                            eventAddcv = Item.geteventaddcv();
                            CallUpdateAndDeleteDialog( Item.geteventid(), Item.geteventdatetimestart(), Item.geteventdatetimeend(), Item.geteventname(), Item.geteventdescription(), Item.geteventlocation(), Item.geteventcontactemail(), Item.getEventcontactphone(), Item.geteventphotoid(), Item.geteventtype(),Item.geteventaddcv(),Item.geteventpaymentvalue());
                        }else {
                            Event Item = eventsById.get(i);
                            EventViewDialog( Item.geteventid(), Item.geteventdatetimestart(), Item.geteventdatetimeend(), Item.geteventname(), Item.geteventdescription(), Item.geteventlocation(), Item.geteventcontactemail(), Item.getEventcontactphone(), Item.geteventphotoid(), Item.geteventtype(),Item.geteventaddcv(),Item.geteventhosterid(),Item.geteventpaymentvalue());
                        }
                    }
                });
                attendedValue="0";
                pd.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    private void CallUpdateAndDeleteDialog(final String eventid, String eventdatetimestart, String eventdatetimeend, String eventname, String eventdescription , String eventlocation, String eventcontactemail, String eventcontactphone, final String eventphotoid, String eventtype,String eventCV,String eventPaymentvalue) {
//        String [] eee = new String[]{"Interview", "Concert"};
        eventId = eventid;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText updateTextEname = (EditText) dialogView.findViewById(R.id.editTextname);
        final EditText updateTextEstime = (EditText) dialogView.findViewById(R.id.editTextStartT);
        final EditText updateTextEetime = (EditText) dialogView.findViewById(R.id.editTextEndT);
        final EditText updateTextEdescription = (EditText) dialogView.findViewById(R.id.editTextDescription);
        final EditText updateTextEElocation = (EditText) dialogView.findViewById(R.id.editTextLocation);
        final EditText updateTextEemail = (EditText) dialogView.findViewById(R.id.editTextContactEmail);
        final EditText updateTextEphone = (EditText) dialogView.findViewById(R.id.editTextPhone);
        final EditText editPaymentUpdate = (EditText) dialogView.findViewById(R.id.editPayment_update);
        final TextView dollarMarkUpdate = (TextView) dialogView.findViewById(R.id.dollarMark_update);
        final Spinner updateTextEtype = (Spinner) dialogView.findViewById(R.id.spinner_type);
        final CheckBox addCVcheckupdate = (CheckBox) dialogView.findViewById(R.id.addCVcheckupdate);
        final CheckBox addPaymentValueUpdate = (CheckBox) dialogView.findViewById(R.id.addPayment_update) ;
        eventImg = (ImageView) dialogView.findViewById( R.id.eventImg ) ;
        final Button selectEventImg = (Button) dialogView.findViewById( R.id.imvSelectEventPhoto ) ;

        editPaymentUpdate.setVisibility(View.INVISIBLE);
        editPaymentUpdate.setText("0");
        dollarMarkUpdate.setVisibility(View.INVISIBLE);

        if(eventAddcv.equals("1")){
            addCVcheckupdate.setChecked(true);
        }else{
            addCVcheckupdate.setChecked(false);
        }

        if(eventPaymentvalue.equals("0")){
            addPaymentValueUpdate.setChecked(false);
        }else{
            addPaymentValueUpdate.setChecked(true);
            editPaymentUpdate.setVisibility(View.VISIBLE);
            dollarMarkUpdate.setVisibility(View.VISIBLE);
        }

        addPaymentValueUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addPaymentValueUpdate.isChecked()){
                    editPaymentUpdate.setVisibility(View.VISIBLE);
                    dollarMarkUpdate.setVisibility(View.VISIBLE);
                    addPaymentvalue = editPaymentUpdate.getText().toString();

                }else{
                    addPaymentvalue = "0";
                    editPaymentUpdate.setText("0");
                    editPaymentUpdate.setVisibility(View.INVISIBLE);
                    dollarMarkUpdate.setVisibility(View.INVISIBLE);
                }
            }
        });

//        if(eventType.equals("Interview")){
            updateTextEtype.setSelection(0);
            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item, values);
            updateTextEtype.setAdapter(adapterSpinner);
//        }else{
//            Collections.reverse(values);
//            ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item, values);
//            updateTextEtype.setAdapter(adapterSpinner);
//        }
        number = "0";

        addCVcheckupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addCVcheckupdate.isChecked()){
                    addCVvalue = "1";
                }else{
                    addCVvalue = "0";
                }
            }
        });

        updateTextEname.setText(eventname);
        updateTextEstime.setText(eventdatetimestart);
        updateTextEetime.setText(eventdatetimeend);
        updateTextEdescription.setText(eventdescription);
        updateTextEElocation.setText(eventlocation);
        updateTextEemail.setText(eventcontactemail);
        updateTextEphone.setText(eventcontactphone);
        editPaymentUpdate.setText(eventPaymentvalue);
        if(eventphotoid.equals( "00" )){
            Picasso.get().load(R.drawable.events).into(eventImg);
        }
        else {
            Picasso.get().load(eventphotoid).into(eventImg);
        }

        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateUser);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteUser);
        final TextView buttonview = (TextView) dialogView.findViewById(R.id.viewusers);

        //TODO; attened users display for hoster
        DatabaseReference usersNum = FirebaseDatabase.getInstance().getReference().child("events").child(eventid).child("eventattendusers");

        usersNum.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String userids = ds.getKey();
                        users.add(userids);
                    }
                    number = String.valueOf(users.size());
                    buttonview.setText("Current users: " + number);


                }else {
                    buttonview.setText("Current user: 0");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dialogBuilder.setTitle(eventname);
        b = dialogBuilder.create();
        b.show();
        b.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                number ="0";
                users.clear();
            }
        });
        selectEventImg.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        } );
        updateTextEstime.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
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
                                updateTextEstime.setText( dates+" "+times );
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
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
        } );
        updateTextEetime.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
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
                                updateTextEetime.setText( datee+" "+timee );
                            }

                        }, mHour, mMinute, false);
                timePickerDialog.show();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
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
        } );

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ename = updateTextEname.getText().toString().trim();
                Estime = updateTextEstime.getText().toString().trim();
                Eetime = updateTextEetime.getText().toString().trim();
                Edescription = updateTextEdescription.getText().toString().trim();
                Elocation = updateTextEElocation.getText().toString().trim();
                Eemail = updateTextEemail.getText().toString().trim();
                Ephone = updateTextEphone.getText().toString().trim();
                Etype = updateTextEtype.getSelectedItem().toString();
                addPaymentvalue = editPaymentUpdate.getText().toString();


                if (!TextUtils.isEmpty(Ename)) {
                    if (!TextUtils.isEmpty(Estime)) {
                        if (!TextUtils.isEmpty(Eetime)) {
                            if (!TextUtils.isEmpty(Edescription)) {
                                if (!TextUtils.isEmpty( Elocation )) {
                                    if (!TextUtils.isEmpty( Eemail )) {
                                        if (!TextUtils.isEmpty( Ephone )) {

                                            if (aaaaaa.equals( "1" )){
                                                uploadImage();
                                            }else{
                                                updateItem( eventId, Estime, Eetime, Ename, Edescription, Elocation, Eemail, Ephone, eventphotoid, Etype ,addCVvalue,addPaymentvalue);
                                                DatabaseReference eventAttend = databaseReference.child(eventId).child("eventattendusers");
                                                String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                DateFormat df = new SimpleDateFormat("yyyy MM dd, HH:mm:ss");
                                                String date = df.format(Calendar.getInstance().getTime());
                                                eventAttend.child(myid).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        users.clear();
                                                        getActivity().finish();
                                                        startActivity(getActivity().getIntent());
                                                        b.dismiss();
                                                    }
                                                });
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        });

        buttonDelete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setTitle("Are you sure you want to delete this event?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteItem(eventid);
                                users.clear();
                                getActivity().finish();
                                startActivity(getActivity().getIntent());
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

        buttonview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                startActivity(getActivity().getIntent());
                Intent intent = new Intent( getActivity(), ViewUsersActivity.class );
                intent.putStringArrayListExtra("userid", (ArrayList<String>) users);
                intent.putExtra("eventid",eventid);
                startActivity( intent );
                b.dismiss();
            }
        });
    }

    public void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        aaaaaa = "1";
    }
    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("application/*");
        startActivityForResult(Intent.createChooser(intent, "Select Files"), PICK_FILE_REQUEST);
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
                            eventImg.setImageResource(0);
                            //TODO; download URL getting
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downURL = uri.toString();
                                    updateItem( eventId, Estime, Eetime, Ename, Edescription, Elocation, Eemail, Ephone, downURL, Etype ,addCVvalue,addPaymentvalue);
                                    DatabaseReference eventAttend = databaseReference.child(eventId).child("eventattendusers");
                                    String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DateFormat df = new SimpleDateFormat("yyyy MM dd, HH:mm:ss");
                                    String date = df.format(Calendar.getInstance().getTime());
                                    eventAttend.child(myid).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            users.clear();
                                            getActivity().finish();
                                            startActivity(getActivity().getIntent());
                                            b.dismiss();
                                        }
                                    });
                                    aaaaaa="0";
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

    public void uploadFile() {

        if(filePathdoc != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            Date currentTime = Calendar.getInstance().getTime();
            final StorageReference ref = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(currentTime.toString()).child(attached_fileName.getText().toString().trim());
            ref.putFile(filePathdoc)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                            //TODO; download URL getting
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downURL_file = uri.toString();
                                    final DatabaseReference eventAttend = databaseReference.child(eventId).child("eventattendusers");
                                    final String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    String filepath = filePathdoc.toString();
                                    String fileurl = downURL_file;
                                    List<String> info = new ArrayList<String>();
                                    info.add(myid);
                                    info.add(filepath);
                                    info.add(fileurl);
                                    eventAttend.child(myid).setValue(info).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "you Attended", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void EventViewDialog(final String eventid, String eventdatetimestart, String eventdatetimeend, String eventname, String eventdescription , String eventlocation, String eventcontactemail, String eventcontactphone, String eventphotoid, String eventtype, final String eventCV, final String eventhosterid, final String eventPayment) {
        eventId = eventid;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.viewevent_dialog, null);
        dialogBuilder.setView(dialogView);
        final TextView updateTextEname = (TextView) dialogView.findViewById(R.id.editTextname);
        final TextView updateTextEstime = (TextView) dialogView.findViewById(R.id.editTextStartT);
        final TextView updateTextEetime = (TextView) dialogView.findViewById(R.id.editTextEndT);
        final TextView updateTextEdescription = (TextView) dialogView.findViewById(R.id.editTextDescription);
        final TextView updateTextEElocation = (TextView) dialogView.findViewById(R.id.editTextLocation);
        final TextView updateTextEemail = (TextView) dialogView.findViewById(R.id.editTextContactEmail);
        final TextView updateTextEphone = (TextView) dialogView.findViewById(R.id.editTextPhone);
        final TextView updateTextEtype = (TextView) dialogView.findViewById(R.id.spinner_type);
        final ImageView viewImg = (ImageView) dialogView.findViewById( R.id.eventViewImg ) ;
        final TextView attached_file = (TextView) dialogView.findViewById(R.id.attached_file);
        attached_fileName = (TextView) dialogView.findViewById(R.id.attached_fileName);
        add_payment_detail = (TextView) dialogView.findViewById(R.id.add_payment_detail);
        buttonAttend = (Button) dialogView.findViewById(R.id.buttonAttend);
        final TextView buttonviewv = (TextView) dialogView.findViewById(R.id.viewusersv);
        number = "0";
        databaseReference.child(eventid).child("eventattendusers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String id = ds.getKey();
                    if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        buttonAttend.setText("Unattend");
                        attendedValue = "1";
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateTextEstime.setText("Start Time : "+eventdatetimestart);
        updateTextEetime.setText("End Time : "+eventdatetimeend);
        updateTextEdescription.setText(""+eventdescription);
        updateTextEElocation.setText("Location : "+eventlocation);
        updateTextEemail.setText("Email : "+eventcontactemail);
        updateTextEphone.setText("Phone Number : "+eventcontactphone);
        updateTextEtype.setText(""+eventtype);
        add_payment_detail.setText("$" + eventPayment);
        if(eventphotoid.equals( "00" )){
            Picasso.get().load(R.drawable.events).into(viewImg);
        }
        else {
            Picasso.get().load(eventphotoid).into(viewImg);
        }

        if(eventCV.equals("1")){
            attached_file.setVisibility(View.VISIBLE);
            attached_fileName.setVisibility(View.VISIBLE);
        }
        else {
            attached_file.setVisibility(View.GONE);
            attached_fileName.setVisibility(View.GONE);
        }

        if(eventPayment.equals("0")){
            add_payment_detail.setVisibility(View.GONE);
        }
        else {
            add_payment_detail.setVisibility(View.VISIBLE);
        }

        attached_fileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFile();
            }
        });

        String yearString = eventdatetimestart.substring(0,4);
        String monthString = eventdatetimestart.substring(5,7);
        String dateString = eventdatetimestart.substring(8,10);

        year = Integer.parseInt(yearString);
        month = Integer.parseInt(monthString);
        day = Integer.parseInt(dateString);

        Log.i("year",String.valueOf(year));
        Log.i("month",String.valueOf(month));
        Log.i("date",String.valueOf(day));

        //TODO; users display on user mode
        DatabaseReference usersNum = FirebaseDatabase.getInstance().getReference().child("events").child(eventid).child("eventattendusers");
        usersNum.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String userids = ds.getKey();
                        users.add(userids);
                    }
                    number = String.valueOf(users.size());
                    buttonviewv.setText("Current users: " + number);
                } else {
                    buttonviewv.setText("Current user: 0");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dialogBuilder.setTitle(eventname);
        final AlertDialog b = dialogBuilder.create();
        b.show();
        b.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                number ="0";
                users.clear();
            }
        });
        buttonAttend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference eventAttend = databaseReference.child(eventid).child("eventattendusers");
                final DatabaseReference hosterid = mrefhoster.child(eventhosterid).child("userattendevents");
                final String myid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(attendedValue.equals("0")){
                    if(eventPayment.equals("0")){
                        if (!eventCV.equals("1")) {
                            DateFormat df = new SimpleDateFormat("yyyy MM dd, HH:mm:ss");
                            String date = df.format(Calendar.getInstance().getTime());

                            eventAttend.child(myid).setValue(date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    hosterid.child(myid).setValue(eventid).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                                            intent.putExtra("notificationId", notificationId);
                                            intent.putExtra("todo", eventid);
                                            PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), 0,
                                                    intent, PendingIntent.FLAG_CANCEL_CURRENT);
                                            AlarmManager alarm = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                            Calendar startTime = Calendar.getInstance();
                                            startTime.set(Calendar.DAY_OF_YEAR,year);
                                            startTime.set(Calendar.DAY_OF_MONTH,month);
                                            startTime.set(Calendar.DATE,day);
                                            startTime.set(Calendar.HOUR_OF_DAY, 6);
                                            startTime.set(Calendar.MINUTE, 0);
                                            startTime.set(Calendar.SECOND, 0);
                                            long alarmStartTime = startTime.getTimeInMillis();
                                            alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);
                                            Toast.makeText(getContext(), "Attended", Toast.LENGTH_SHORT).show();
                                            users.clear();
                                            b.dismiss();
                                            attendedValue="0";
                                        }
                                    });
                                }
                            });
                        }else if(!TextUtils.isEmpty(attached_fileName.getText().toString()))
                        {
                            uploadFile();
                            users.clear();
                            b.dismiss();
                            attendedValue="0";
                        }
                        else{
                            Toast.makeText(getContext(), "Please Upload your Doc.", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        if (!eventCV.equals("1")) {
                            fileandpaymentValue = "0";
                            Intent intent=new Intent(getActivity(), PaypalActivity.class);
                            intent.putExtra("paymentValue",eventPayment);
                            intent.putExtra("eventID",eventid);
                            intent.putExtra("eventhosterID",eventhosterid);
                            intent.putExtra("fileandpaymentValue",fileandpaymentValue);
                            startActivity(intent);
                            b.dismiss();
                            attendedValue="0";
                        }else if(!TextUtils.isEmpty(attached_fileName.getText().toString())){
                            fileandpaymentValue = "1";
                            String Attached_fileName = attached_fileName.getText().toString();
                            Intent intent=new Intent(getActivity(), PaypalActivity.class);
                            intent.putExtra("paymentValue",eventPayment);
                            intent.putExtra("eventID",eventid);
                            intent.putExtra("eventhosterID",eventhosterid);
                            intent.putExtra("fileandpaymentValue",fileandpaymentValue);
                            intent.putExtra("filePathdoc",filePathdoc);
                            intent.putExtra("eventId",eventId);
                            intent.putExtra("Attached_fileName",Attached_fileName);

                            startActivity(intent);
                            b.dismiss();
                            attendedValue="0";

                        }else{
                            Toast.makeText(getContext(), "Please Upload your Doc.", Toast.LENGTH_SHORT).show();
                        }





                    }
                }else{
                    if(eventPayment.equals("0")) {
                        eventAttend.child(myid).removeValue();
                        Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                        b.dismiss();
                    }else{
                        Toast.makeText(getContext(), "You can not cancel.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private boolean updateItem(String eventid,  String Estime,  String Eetime,  String Ename, String Edescription , String Elocation, String Eemail, String Ephone, String downURL, String Etype,String addCV,String addPaymentValue) {
        DatabaseReference UpdateReference = FirebaseDatabase.getInstance().getReference("events").child(eventid);
        Event Item = new Event( eventid, Estime, Eetime, Ename, Edescription, Elocation, Eemail, Ephone, downURL, Etype,addCV,userId ,addPaymentValue);
        UpdateReference.setValue( Item );
        Toast.makeText(getContext(), "Event Updated", Toast.LENGTH_LONG).show();
        listViewEvents.setAdapter(null);
        oncreate = 1;
        return true;
    }
    private boolean deleteItem(String eventid) {
        DatabaseReference DeleteReference = FirebaseDatabase.getInstance().getReference("events").child(eventid);
        DeleteReference.removeValue();
        Toast.makeText(getContext(), "Event Deleted", Toast.LENGTH_LONG).show();
        oncreate = 1;
        return true;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                eventImg.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if(requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePathdoc = data.getData();
                Uri returnUri = data.getData();
                String src = returnUri.getPath();
                String file = src.substring(src.lastIndexOf("/")+1, src.length());
                attached_fileName.setText(file);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        databaseReference.removeEventListener( mValueEventListener );
        oncreate = 0;
        values.clear();
    }
}