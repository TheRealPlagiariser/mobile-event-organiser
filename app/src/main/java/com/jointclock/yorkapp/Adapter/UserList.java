package com.jointclock.yorkapp.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.jointclock.yorkapp.Model.User;
import com.jointclock.yorkapp.R;
import com.squareup.picasso.Picasso;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserList extends ArrayAdapter<User> {

    private Activity context;
    List<User> Users;

    public UserList(Activity context, List<User> Users) {
        super(context, R.layout.layout_item_users, Users);
        this.context = context;
        this.Users = Users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItemu = inflater.inflate(R.layout.layout_item_users, null, true);

        TextView textViewfirstName = (TextView) listViewItemu.findViewById(R.id.txvuserfName);
        TextView textViewlastName = (TextView) listViewItemu.findViewById(R.id.txvuserlName);
        TextView textviewemail = (TextView) listViewItemu.findViewById(R.id.txvuserEmail);
        TextView textviewnumber = (TextView) listViewItemu.findViewById(R.id.txvuserPhone);
        TextView textViewresnumberB = (TextView) listViewItemu.findViewById( R.id.resnumberB );
        CircleImageView userphoto = (CircleImageView) listViewItemu.findViewById( R.id.imvuserPhoto );

        User User = Users.get(position);
        textViewfirstName.setText(User.getuserfirstname());
        textViewlastName.setText(User.getuserlastname());
        textviewemail.setText(User.getuseremail());
        textviewnumber.setText(User.getuserphone());
        if(User.getusertype().equals( "1" )){
            textViewresnumberB.setText( "( Hoster )" );
        }
        else {
            textViewresnumberB.setText( "" );
        }
        if(User.getuserphotoid().equals( "00" )){
            Picasso.get().load(R.drawable.avatar).into(userphoto);
        }
        else {
            Picasso.get().load(User.getuserphotoid()).into(userphoto);
        }
        return listViewItemu;
    }
}
