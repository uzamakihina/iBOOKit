/**
 * Class name: RequestFuncTest
 *
 * version 1.0
 *
 * Date: March 9, 2019
 *
 * Copyright (c) Team 13, Winter, CMPUT301, University of Alberta
 */
package com.example.ibookit;

import com.example.ibookit.Functionality.RequestStatusHandler;
import com.example.ibookit.Model.Request;
import com.example.ibookit.Model.RequestR;
import com.example.ibookit.Model.RequestS;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class RequestFunc {

    RequestStatusHandler ReqStat = new RequestStatusHandler();
    Request request = new Request();
    ArrayList<Request> requestlist = new ArrayList<Request>(); //marcos

    //Context context, int resource, ArrayList<Request> objects)
    @Test
    public void requestTest(){
        requestlist.add(request);

        RequestR testRequestRecieve= new RequestR("testname", requestlist);



        RequestS requestsender = new RequestS(requestlist);


        assertNotEquals(requestsender, null);
        assertEquals(ReqStat.StatusIntegerToString(2), "Declined");
        assertEquals(ReqStat.StatusIntegerToString(0), "Pending");
        assertEquals(ReqStat.StatusIntegerToString(1), "Accepted");
        assertEquals(ReqStat.StatusIntegerToString(4), "StatusString: Out of range");
        assertEquals(requestsender.sentRequests(), requestlist);

        ArrayList<Request> testlist = testRequestRecieve.requestReceiveList();
        Request testquest = testlist.get(0);


        assertEquals(testquest, request);




    }









}