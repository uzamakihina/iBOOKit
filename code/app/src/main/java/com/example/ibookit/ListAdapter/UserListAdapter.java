/**
 * Class name: UserListAdapter
 *
 * version 1.0
 *
 * Date: March 9, 2019
 *
 * Copyright (c) Team 13, Winter, CMPUT301, University of Alberta
 */
package com.example.ibookit.ListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ibookit.Model.User;
import com.example.ibookit.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class UserListAdapter extends ArrayAdapter<User> {

    private Context mContext;
    private int mResource;
    private TextView mUsername, mEmail, mPhone;
    private ImageView imageView;

    public UserListAdapter(Context context, int resource, ArrayList<User> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    /**
     * reference: https://stackoverflow.com/questions/8166497/custom-adapter-for-list-view
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
        }

        User user = getItem(position);

        if (user != null) {
            mUsername = convertView.findViewById(R.id.listUsername);
            mEmail = convertView.findViewById(R.id.listEmail);
            mPhone = convertView.findViewById(R.id.listPhoneNumber);
            imageView = convertView.findViewById(R.id.imageUser);

            mUsername.setText(user.getUsername());
            mEmail.setText(user.getEmail());
            mPhone.setText(user.getPhoneNumber());

            setImage(user.getImageURL(), imageView);
        }

        return convertView;
    }

    /**
     * set image for user profile in custom listView
     * @param path
     * @param imageView
     */
    private void setImage(String path, final ImageView imageView) {
        if (path != null) {
            Picasso.get().load(path).fit().centerCrop().into(imageView);
        } else {
            imageView.setImageResource(R.drawable.users);
        }
    }
}
