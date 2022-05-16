package com.jointclock.yorkapp.ui.myevents;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jointclock.yorkapp.Adapter.EventList;
import com.jointclock.yorkapp.Model.Event;
import com.jointclock.yorkapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyEventsFragment extends Fragment{

    DatabaseReference databaseReference;
    ListView listViewEvents;
    List<Event> events = new ArrayList<>();
    private EventList EventAdapter;
    ProgressDialog pd;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate( R.layout.fragment_myevents, container, false );
        databaseReference = FirebaseDatabase.getInstance().getReference("events");
        listViewEvents = (ListView) root.findViewById( R.id.listViewEvents );

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();
        EventAdapter = new EventList(getActivity(), events );
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(mValueEventListener);
    }

    ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            events.clear();
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                final Event event = postSnapshot.getValue( Event.class);
                databaseReference.child(event.geteventid()).child("eventattendusers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String id = ds.getKey();
                            if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                events.add( event );
                                break;
                            }
                        }
                        listViewEvents.invalidateViews();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            Collections.sort(events, new Comparator<Event>() {

                @Override
                public int compare(Event o1, Event o2) {
                    // TODO Auto-generated method stub
                    return o1.geteventdatetimestart().compareTo(o2.geteventdatetimestart());
                }
            });
            listViewEvents.setAdapter(EventAdapter);
            pd.dismiss();
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

}