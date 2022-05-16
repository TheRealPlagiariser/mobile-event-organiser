package com.jointclock.yorkapp.Adapter;


import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import com.jointclock.yorkapp.Model.Event;
import com.jointclock.yorkapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EventList extends ArrayAdapter<Event> {

    private Activity context;

    List<Event> events;

    public EventList(Activity context, List<Event> events) {
        super(context, R.layout.layout_item_list, events );
        this.context = context;
        this.events = events;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_item_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textviewstarttime = (TextView) listViewItem.findViewById(R.id.textviewstarttime);
        TextView textviewendtime = (TextView) listViewItem.findViewById(R.id.textviewendtime);
        TextView textviewtype = (TextView) listViewItem.findViewById(R.id.textviewtype);
        ImageView eventphoto = (ImageView) listViewItem.findViewById( R.id.imvEventPhoto );

        eventphoto.setClipToOutline(true);

        Event Item = events.get(position);
        textViewName.setText( Item.geteventname());
        textviewstarttime.setText( Item.geteventdatetimestart());
        textviewendtime.setText( Item.geteventdatetimeend());
        textviewtype.setText( Item.geteventtype() );

        if(Item.geteventphotoid().equals( "00" )){
            Picasso.get().load(R.drawable.events).into(eventphoto);
        }
        else {
            Picasso.get().load(Item.geteventphotoid()).into(eventphoto);
        }

        return listViewItem;
    }

}