/**
 * Class name: MyShelfBorrowerActivity
 *
 * version 1.2
 *
 * Date: March 30, 2019
 *
 * Copyright (c) Team 13, Winter, CMPUT301, University of Alberta
 */
package com.example.ibookit.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ibookit.ListAdapter.BookListAdapter;
import com.example.ibookit.Model.Book;
import com.example.ibookit.Model.BorrowerShelf;
import com.example.ibookit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MyShelfBorrowerActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayAdapter<Book> adapter;
    private ArrayList<Book> mBooks = new ArrayList<>();
    private BorrowerShelf borrowerShelf = new BorrowerShelf();
    private Button changeShelf;
    private final Integer BorrowScanRequestCode = 1000;
    private final Integer ReturnScanRequestCode = 1001;

    /**
     * Showing the borrower shelf in a listView
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myshelf_borrowed);

        mListView = findViewById(R.id.BorrowerShelfListView);

        changeShelf = findViewById(R.id.my_book);


        changeShelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyShelfBorrowerActivity.this, MyShelfOwnerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        ListViewClickHandler();

        setBottomNavigationView();

    }


    @Override
    protected void onStart() {
        super.onStart();

        adapter = new BookListAdapter(this, R.layout.adapter_book, mBooks);
        mListView.setAdapter(adapter);
        borrowerShelf.SyncBookShelf(mBooks, adapter, 3);
        mListView.setClickable(true);

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
        inflater.inflate(R.menu.borrower_scan_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.Borrower_borrow:
                Intent scan1 = new Intent(MyShelfBorrowerActivity.this, ScannerActivity.class);
                startActivityForResult(scan1, BorrowScanRequestCode);
                return true;

            case R.id.Borrower_return:

                Intent scan2 = new Intent(MyShelfBorrowerActivity.this, ScannerActivity.class);
                startActivityForResult(scan2, ReturnScanRequestCode);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

                
            }
        });
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
                        Intent add = new Intent(MyShelfBorrowerActivity.this, AddBookOwnerActivity.class);
                        add.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(add);
                        break;

                    case R.id.action_home:
                        Intent home = new Intent(MyShelfBorrowerActivity.this, HomeSearchActivity.class);
                        home.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(home);

                        break;

                    case R.id.action_myshelf:

                        break;

                    case R.id.action_profile:
                        Intent profile = new Intent(MyShelfBorrowerActivity.this, UserProfileActivity.class);
                        profile.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(profile);
                        break;

                    case R.id.action_request:
                        Intent request = new Intent(MyShelfBorrowerActivity.this, RequestChActivity.class);
                        request.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(request);

                        break;
                }

                return false;
            }
        });
    }


    /**
     * scanning isbn for borrow a book
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (requestCode == BorrowScanRequestCode && resultCode == RESULT_OK ){

            final String scannedISBN = data.getStringExtra("scanned_ISBN");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String username = user.getDisplayName();

            //todo: accesing requestRef than request sent under user make structure clearer, but increased check clause on if statement might slow down the app
            // requestRef changed by zijun
            final DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("users").child(username).child("requestSent");
            final DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference().child("books");

            bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                            if (d.child("isbn").getValue().toString().equals(scannedISBN)
                                && d.hasChild("transitStatus")
                                && d.child("transitStatus").getValue().toString().equals("1")){
                                final String bookID = d.getKey();
                                final Book targetBook = d.getValue(Book.class);
                                requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot d:dataSnapshot.getChildren()){
                                        if (d.child("bookId").getValue().toString().equals(bookID)){

                                            // add book to borrower shelf, add book method updates
                                            // the directories that needs to be updated
                                            Toast.makeText(MyShelfBorrowerActivity.this, "Book borrowed",
                                                    Toast.LENGTH_SHORT).show();
                                            borrowerShelf.add_book(targetBook);
                                            borrowerShelf.SyncBookShelf(mBooks, adapter, 3);
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

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else if (requestCode == ReturnScanRequestCode && resultCode == RESULT_OK ){

            final String scannedISBN = data.getStringExtra("scanned_ISBN");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String username = user.getDisplayName();

            final DatabaseReference borrowerShelfRef = FirebaseDatabase.getInstance().getReference().child("users").child(username).child("borrowerShelf");
            final DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference().child("users").child(username).child("requestSent");

            borrowerShelfRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot d: dataSnapshot.getChildren()){
                        if (d.child("isbn").getValue().toString().equals(scannedISBN)){
                            //todo: what if user have borrowed two book with same isbn
                            final Book targetBook = d.getValue(Book.class);
                            borrowerShelf.remove_book(targetBook);
                            borrowerShelf.SyncBookShelf(mBooks, adapter, 3);

                            Toast.makeText(MyShelfBorrowerActivity.this, "Book returned",
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
