package com.ktc.integration.twod.integrationutilities.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;


public class Util {

    private static final Logger log = LogManager.getLogger(Util.class);

    public static String ConvertDateToString (Date dateIn){
        String dateOut = "";

        DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        dateOut = sdf1.format(dateIn);;


        return dateOut; //will be yyyy/MM/dd
    }

}
