package com.example.ibookit.View;

import android.content.Intent;
import android.support.annotation.NonNull;

import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ibookit.Model.User;
import com.example.ibookit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends AppCompatActivity {

    private static final String TAG = "UserProfileActivity";
    private TextView mUsername, mEmail;
    private Button email,edit,signout;
    private ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        setBottomNavigationView();
        setInformation();
        configure_buttons();

        //this used for show user search result
        Intent intent = getIntent();
        String objStr = intent.getStringExtra("UserResult");
        if (objStr != null){
            Gson gson = new Gson();
            User sUser = gson.fromJson(objStr, User.class);
            setOtherUserInformation(sUser);
            edit.setVisibility(View.GONE);
            signout.setVisibility(View.GONE);
        }


    }


    private void configure_buttons(){
        email = (Button) findViewById(R.id.contactInfo_user);
        edit =  findViewById(R.id.edit_profile);
        signout = findViewById(R.id.signout_profile);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, ContactInformationActivity.class);
                startActivity(intent);
            }
        });

        //edit button added to allow edit on user profile
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, ContactInformationActivity.class);
                startActivity(intent);
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
                Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


    }

    private void setInformation() {
        mUsername = findViewById(R.id.userName_userProfile);
        mEmail = findViewById(R.id.contactInfo_user);
        imageView = findViewById(R.id.profilePic_userProfile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mUsername.setText(user.getDisplayName());
        mEmail.setText(user.getEmail());

        setUserImage(user, imageView);

    }

    private void setUserImage(FirebaseUser user, final ImageView imageView) {
        String username = user.getDisplayName();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(username);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userClass = dataSnapshot.getValue(User.class);

                Picasso.get().load(userClass.getImageURL()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setOtherUserInformation(User mUser){
        mUsername = findViewById(R.id.userName_userProfile);
        mEmail = findViewById(R.id.contactInfo_user);
        mUsername.setText(mUser.getUsername());
        mEmail.setText(mUser.getEmail());
    }



    private void signout() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "signout: " + user.getDisplayName());
        FirebaseAuth.getInstance().signOut();
    }


    private void setBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_add:
                        Intent add = new Intent(UserProfileActivity.this, AddBookAsOwnerActivity.class);
                        add.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(add);
                        break;

                    case R.id.action_home:
                        Intent home = new Intent(UserProfileActivity.this, HomeSearchActivity.class);
                        home.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(home);

                        break;

                    case R.id.action_myshelf:
                        Intent myshelf = new Intent(UserProfileActivity.this, MyShelfOwnerActivity.class);
                        myshelf.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(myshelf);
                        break;

                    case R.id.action_profile:
                        break;

                    case R.id.action_request:
                        Intent request = new Intent(UserProfileActivity.this, CheckRequestsActivity.class);
                        request.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(request);

                        break;
                }

                return false;
            }
        });
    }



}
