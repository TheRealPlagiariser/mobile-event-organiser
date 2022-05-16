package com.jointclock.yorkapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Book(NClock) on 2020/03/27.
 */

public class AlarmReceiver extends BroadcastReceiver {
    DatabaseReference databaseReference;
    @Override
    public void onReceive(final Context context, Intent intent) {
        databaseReference = FirebaseDatabase.getInstance().getReference("events");
        final int notificationId = intent.getIntExtra("notificationId", 0);
        String eventid = intent.getStringExtra("todo");
        databaseReference.child(eventid).child("eventname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String eventName = dataSnapshot.getValue().toString();
                Intent mainIntent = new Intent(context, MainActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);
                NotificationManager myNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(context);
                builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("It's Time!")
                        .setContentText("<"+eventName+">  will be started today !")
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_ALL);
                myNotificationManager.notify(notificationId, builder.build());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}

