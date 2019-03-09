package com.example.ibookit.Model;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestReceived {
    private static final String TAG = "RequestReceived";
    private ArrayList<Request> requestSent = new ArrayList<>();
    private DatabaseReference mDatabase;
    private DatabaseReference bDatabase;
    public String username;
    private ArrayList<String> last = new ArrayList<>();
    private String bookTitle;
    public ArrayList<String> currentIds = new ArrayList<String>();


    public ArrayList<Request> getRequestSent() {
        return requestSent;
    }

    public void setRequestSent(ArrayList<Request> requestSent) {
        this.requestSent = requestSent;
    }

    public ArrayList<String> getIds(){
        return currentIds;
    }

    public RequestReceived(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        username = user.getDisplayName();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(username).child("requestReceived");
        bDatabase = FirebaseDatabase.getInstance().getReference().child("books");
    }

    public void RetriveBook(final ArrayList<String> bookList,final ArrayAdapter<String> adapter) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                last.clear();
                bookList.clear();
                adapter.notifyDataSetChanged();
                for (DataSnapshot d: dataSnapshot.getChildren()) {

                    Request request = d.getValue(Request.class);

                    if (!last.contains(request.getBookId())){
                        final DatabaseReference bDatabase = FirebaseDatabase.getInstance().getReference().child("books").child(request.getBookId());
                        bDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Book book = dataSnapshot.getValue(Book.class);
                                bookList.add(book.getTitle());
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        last.add(request.getBookId());
                    }
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });

    }

    public void RequestInBook(final ArrayList<String> users,final ArrayAdapter<String> adapter,final String bookname){



        users.clear();
        currentIds.clear();
        adapter.notifyDataSetChanged();

        mDatabase.addValueEventListener(new ValueEventListener() {






            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                currentIds.clear();
                adapter.notifyDataSetChanged();
                for (DataSnapshot d: dataSnapshot.getChildren()) {
                    final Request request = d.getValue(Request.class);
                    bDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Book book1 = ds.getValue(Book.class);
                                bookTitle = book1.getTitle();
                                if (bookTitle.equals(bookname)) {
                                    // actually requestid


                                    users.add(request.getSender());
                                    currentIds.add(request.getRid());

                                    adapter.notifyDataSetChanged();

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });



                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });

    }



}
