/**
 * Class name: UserSearch
 *
 * version 1.0
 *
 * Date: March 9, 2019
 *
 * Copyright (c) Team 13, Winter, CMPUT301, University of Alberta
 */
package com.example.ibookit.Functionality;

import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.example.ibookit.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserSearch {
    private ArrayList<User> mResult;

    public UserSearch(){}

    /**
     * Search the user by keywords
     * and put the result into ListView
     *
     * reference: https://stackoverflow.com/questions/10827872/difference-between-string-replace-and-replaceall
     *
     * @param mKeyword
     * @param result
     * @param adapter
     */
    public void searchByKeyword(final String mKeyword, final ArrayList<User> result, final ArrayAdapter<User> adapter) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    result.clear();
                    adapter.notifyDataSetChanged();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        if (d.getKey().toLowerCase().contains
                                (mKeyword.replaceAll("\\s+","").toLowerCase())){
                            User temp = d.getValue(User.class);
                            result.add(temp);
                            setResult(result);
                            adapter.notifyDataSetChanged();
                        }
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * Get search result
     * @return
     */
    public ArrayList<User> getResult() {
        return mResult;
    }
    public void setResult(ArrayList<User> result) {
        this.mResult = result;
    }

}
