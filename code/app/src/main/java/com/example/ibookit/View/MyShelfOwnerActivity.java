/**
 * Class name: MyShelfOwnerActivity
 *
 * version 1.2
 *
 * Date: March 30, 2019
 *
 * Copyright (c) Team 13, Winter, CMPUT301, University of Alberta
 */
package com.example.ibookit.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.Toast;

import android.widget.ListView;

import com.example.ibookit.ListAdapter.BookListAdapter;
import com.example.ibookit.Model.Book;
import com.example.ibookit.Model.OwnerShelf;

import com.example.ibookit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;


public class MyShelfOwnerActivity extends AppCompatActivity {
    public static Context sContext;

    private static final String TAG = "MyShelfOwnerActivity";
    private ListView mListView;
    private Button chooseAvailable, chooseRequested, chooseAccepted, chooseBorrowed, myshelf;
    private ArrayAdapter<Book> adapter;
    private ArrayList<Book> mBooks = new ArrayList<>();
    private OwnerShelf ownerShelf = new OwnerShelf();
    private Integer status;
    private final Integer LendScanRequestCode = 1000;
    private final Integer ReceiveScanRequestCode = 1001;
    private Book CurrentProcessLending;

    /**
     * Showing owner shelf in a listView
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myshelf_mybook);

        chooseAvailable = findViewById(R.id.myshlf_available);
        chooseRequested = findViewById(R.id.myshlf_requested);
        chooseAccepted = findViewById(R.id.myshelf_accepted);
        chooseBorrowed = findViewById(R.id.myshelf_borrowed);
        myshelf = findViewById(R.id.my_book);

        mListView = findViewById(R.id.bookListView);
        Button changeShelf = findViewById(R.id.borrowed);

        changeShelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyShelfOwnerActivity.this, MyShelfBorrowerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        // Book status selector
        chooseAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 0;
                ownerShelf.SyncBookShelf(mBooks, adapter, status);
            }
        });

        chooseRequested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 1;
                ownerShelf.SyncBookShelf(mBooks, adapter, status);
            }
        });

        chooseAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 2;
                ownerShelf.SyncBookShelf(mBooks, adapter, status);
            }
        });

        chooseBorrowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = 3;
                ownerShelf.SyncBookShelf(mBooks, adapter, status);
            }
        });

        myshelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = -1;
                ownerShelf.SyncBookShelf(mBooks, adapter, status);
            }
        });

        setBottomNavigationView();

        ListViewClickHandler();


    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter = new BookListAdapter(this, R.layout.adapter_book, mBooks);
        mListView.setAdapter(adapter);
        mListView.setClickable(true);
        ownerShelf.SyncBookShelf(mBooks, adapter, -1); // -1 means let listView showing all books

    }

    /**
     * scan code on menu bar
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.owner_scan_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.owner_lend:
                Intent scan1 = new Intent(MyShelfOwnerActivity.this, ScannerActivity.class);
                startActivityForResult(scan1, LendScanRequestCode);
                return true;

            case R.id.owner_receive:
                Intent scan2 = new Intent(MyShelfOwnerActivity.this, ScannerActivity.class);
                startActivityForResult(scan2, ReceiveScanRequestCode);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Navigation bar enabled
     */
    private void setBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_add:
                        Intent add = new Intent(MyShelfOwnerActivity.this, AddBookOwnerActivity.class);
                        add.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(add);
                        break;

                    case R.id.action_home:
                        Intent home = new Intent(MyShelfOwnerActivity.this, HomeSearchActivity.class);
                        home.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(home);

                        break;

                    case R.id.action_myshelf:
                        break;

                    case R.id.action_profile:
                        Intent profile = new Intent(MyShelfOwnerActivity.this, UserProfileActivity.class);
                        profile.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(profile);
                        break;

                    case R.id.action_request:
                        Intent request = new Intent(MyShelfOwnerActivity.this, RequestChActivity.class);
                        request.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(request);

                        break;
                }

                return false;
            }
        });
    }

    /**
     * Handle user clicking item on the list
     */
    private void ListViewClickHandler () {
        final ListView finalList = mListView;
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = (Book) finalList.getItemAtPosition(position);


                // todo: update or delete if this book is not in available status, need to update this info under all requests

                setDialog(book);

            }
        });
    }

    /**
     * set a dialog when clicking item
     * choosing if user want to cancel or view book information
     * @param book
     */
    private void setDialog(final Book book) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Choose an action");
        builder.setCancelable(true);

        builder.setPositiveButton("View", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MyShelfOwnerActivity.this, BookInfoOwnerActivity.class);
                Gson gson = new Gson();

                String out = gson.toJson(book);

                intent.putExtra("book", out);
                startActivity(intent);
            }
        });


        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //if book is not borrowed
                if (book.getStatus() == 0 ) {
                    ownerShelf.remove_book(book);
                    Toast.makeText(MyShelfOwnerActivity.this, "Book deleted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyShelfOwnerActivity.this, "Can't delete this book",
                            Toast.LENGTH_SHORT).show();
                }
                
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    /**
     * scanning for owner
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (requestCode == LendScanRequestCode && resultCode == RESULT_OK ){

            final String scannedISBN = data.getStringExtra("scanned_ISBN");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String username = user.getDisplayName();

            final DatabaseReference ownShelfRef = FirebaseDatabase.getInstance().getReference().child("users").child(username).child("ownerShelf");
            // requestRef changed by zijun
            final DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("users").child(username).child("requestReceived");


            ownShelfRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d:dataSnapshot.getChildren()){
                        // else the book does not exit in owner shelf or request is not accepted
                        // book status of 2 stands for accepted
                        if (d.child("isbn").getValue().toString().equals(scannedISBN)
                            && d.child("status").getValue().toString().equals("2")
                            && d.child("transitStatus").getValue().toString().equals("0")){

                            //todo: what if user have two different book with same isbn, and both of
                            //them are requested and accepted, this will set both of them to lend pending

                            final String bookID = d.getKey();
                            final Book targetBook = d.getValue(Book.class);
                            requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot d:dataSnapshot.getChildren()){
                                        if (d.child("bookId").getValue().toString().equals(bookID)){

                                            Toast.makeText(MyShelfOwnerActivity.this, "book lend out",
                                                Toast.LENGTH_SHORT).show();
                                            // set transit status
                                            targetBook.setTransitStatus(1);
                                            // update book in corresponding directories
                                            ownerShelf.update_book(targetBook);
                                            ownerShelf.SyncBookShelf(mBooks, adapter, -1);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
//
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else if (requestCode == ReceiveScanRequestCode && resultCode == RESULT_OK ){

            final String scannedISBN = data.getStringExtra("scanned_ISBN");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String username = user.getDisplayName();

            final DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference().child("books");
            final DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("users").child(username).child("requestReceived");

            booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        if (d.child("isbn").getValue().toString().equals(scannedISBN)
                            && d.child("owner").getValue().toString().equals(username)
                            && d.child("transitStatus").getValue().toString().equals("2")){
                            final Book targetBook = d.getValue(Book.class);
                            targetBook.setTransitStatus(0);
                            targetBook.setCurrentBorrower("");
                            ownerShelf.update_book(targetBook);
                            ownerShelf.SyncBookShelf(mBooks, adapter, -1);
                            requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot d: dataSnapshot.getChildren()){
                                        if (d.child("bookId").getValue().toString().equals(targetBook.getId())){

                                            // delete request sent from all sender
                                            String senderName = d.child("sender").getValue().toString();
                                            DatabaseReference senderRequestRef = FirebaseDatabase.getInstance().getReference().child("users").child(senderName).child("requestSent");
                                            senderRequestRef.child(d.getKey()).removeValue();

                                            DatabaseReference locationRef = FirebaseDatabase.getInstance().getReference().child("locations");
                                            locationRef.child(d.getKey()).removeValue();


                                            //delete request received on book on owner's account
                                            requestRef.child(d.getKey()).removeValue();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            Toast.makeText(MyShelfOwnerActivity.this, "Book Received",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    }



}
