package com.example.ibookit;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.ibookit.Model.Book;
import com.example.ibookit.Model.BorrowerShelf;
import com.example.ibookit.Model.OwnerShelf;
import com.google.firebase.FirebaseApp;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
/**
 * @author zisen zhou
 *
 * @version 1.0
 */

@RunWith(AndroidJUnit4.class)
public class BorrowerShelfTest {
    private Context instrumentationCtx;
    private BorrowerShelf testShelf = new BorrowerShelf();

    @Test
    public void test(){
        instrumentationCtx = InstrumentationRegistry.getContext();
        FirebaseApp.initializeApp(instrumentationCtx);

        //test All_Books method in BorrowerShelf
        ArrayList<Book> expected= new ArrayList<>();
        assertEquals(expected, testShelf.All_books());

    }
}
