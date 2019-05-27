/**
 * Class name: AddBookOwnerActivity
 *
 * version 1.0
 *
 * Date: March 9, 2019
 *
 * Copyright (c) Team 13, Winter, CMPUT301, University of Alberta
 */
package com.example.ibookit.View;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ibookit.Model.User;
import com.example.ibookit.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class ContactInformationActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private Button saveChange;
    private TextView mUsername, mEmail, mPhone;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private FirebaseUser user;
    private DatabaseReference mDatabase;

    /**
     *let User View or edit user profile in UI
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_information);

        imageButton = findViewById(R.id.image_contact_Edit);
        saveChange = findViewById(R.id.saveChanges_userProfile);
        mUsername = findViewById(R.id.username_contact);
        mEmail = findViewById(R.id.email_contact);
        mPhone = findViewById(R.id.phone_contact);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String username = user.getDisplayName();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(username);

        getUserInfo(mUsername, mEmail, mPhone, imageButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooser();
            }
        });

        saveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("phoneNumber").setValue(mPhone.getText().toString());
                setUserImage(mDatabase);

                Toast.makeText(ContactInformationActivity.this, "Submitted",
                        Toast.LENGTH_SHORT).show();


                finish();
            }
        });


    }

    /**
     * pick a image for the book in system
     *
     * reference: https://codinginflow.com/tutorials/android/firebase-storage-upload-and-retrieve-images/part-2-image-chooser
     */
    private void fileChooser () {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).fit().centerCrop().into(imageButton);

        }

    }

    /**
     * Get login user information from FireBase
     *
     * @param mUsername
     * @param mEmail
     * @param mPhone
     * @param imageButton
     */
    private void getUserInfo (final TextView mUsername, final TextView mEmail, final TextView mPhone, final ImageButton imageButton) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userClass = dataSnapshot.getValue(User.class);
                mUsername.setText(userClass.getUsername());
                mEmail.setText(userClass.getEmail());
                mPhone.setText(userClass.getPhoneNumber());

                Picasso.get().load(userClass.getImageURL()).into(imageButton);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Set User image in user profile
     *
     * @param mDatabase
     */
    private void setUserImage(final DatabaseReference mDatabase) {
        if (mImageUri != null) {
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
            final StorageReference fileRef = mStorageRef.child("users").child(user.getDisplayName());


            fileRef.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        mDatabase.child("imageURL").setValue(downloadUri.toString());
                    }

                }
            });

        }
    }
}
