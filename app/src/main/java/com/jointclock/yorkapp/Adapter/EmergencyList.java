package com.jointclock.yorkapp.Adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.jointclock.yorkapp.Model.Emergency;
import com.jointclock.yorkapp.R;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EmergencyList extends ArrayAdapter<Emergency> {

    private Activity context;
    List<Emergency> emergency;

    public EmergencyList(Activity context, List<Emergency> emergency) {
        super(context, R.layout.layout_item_taxi_doctor, emergency);
        this.context = context;
        this.emergency = emergency;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItemu = inflater.inflate(R.layout.layout_item_taxi_doctor, null, true);

        TextView emgfirstName = (TextView) listViewItemu.findViewById(R.id.emgfName);
        TextView emglastName = (TextView) listViewItemu.findViewById(R.id.emglName);
        TextView emgType = (TextView) listViewItemu.findViewById( R.id.emgType );
        final CircleImageView emgphoto = (CircleImageView) listViewItemu.findViewById( R.id.emgPhoto );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[][] imageBytes = {baos.toByteArray()};
        final Emergency User = emergency.get(position);
        emgfirstName.setText(User.getemergencyfirstname());
        emglastName.setText(User.getemergencylastname());
        emgType.setText(User.getemergencytype());
        imageBytes[0] = Base64.decode(User.getemergencyphotoid(), Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes[0], 0, imageBytes[0].length);
        emgphoto.setImageBitmap(decodedImage);

        return listViewItemu;
    }
}
