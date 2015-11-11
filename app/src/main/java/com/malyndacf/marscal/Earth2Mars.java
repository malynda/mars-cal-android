package com.malyndacf.marscal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by malynda on 4/14/15.
 */
public class Earth2Mars extends ActionBarActivity {

    private double marsOutputDate;
    private int marsOutputYear;
    private GregorianCalendar earthInputDate = new GregorianCalendar();

    private double solsinamarsyear=668.6; // number of sols in a martian year
    private double sollength=88775.245; // sol length, in seconds
    private double daylength=86400; // Earth day length, in seconds
    private double yearlength=365.2422; // number of earth days in an earth year

    private double ref_marsyear=26;
    private double ref_jdate=2452383.23; // Julian date for April 18.7 2002, Ls=0, beginning of Mars Year 26

    private double year_day=668.6; // number of sols in a martian year
    private double e_ellip=0.09340; // orbital ecentricity
    private double peri_day=485.35; // perihelion date (in sols)
    private double timeperi=1.90258341759902; // 2*Pi*(1-Ls(perihelion)/360); Ls(perihelion)=250.99
    private double rad2deg=180./Math.PI;

    DecimalFormat ls_format = new DecimalFormat("#.##");

    TextView marsDate;
    DatePicker earthDate;

    String theTag = "E2M";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earth2mars);

        marsDate = (TextView)findViewById(R.id.marsDateOutput);
        earthDate = (DatePicker)findViewById(R.id.earthDateInput);

        Button calcMars2Earth = (Button) findViewById(R.id.bCalcEarth2Mars);
        calcMars2Earth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                calcMarsDate(earthDate);
            }
        });

    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        // Use the current date as the default date in the picker
//        final Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int month = c.get(Calendar.MONTH);
//        int day = c.get(Calendar.DAY_OF_MONTH);
//
//        // Create a new instance of DatePickerDialog and return it
//        return new DatePickerDialog(getActivity(), this, year, month, day);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        switch (item.getItemId()) {
            case R.id.earthDateInput:
                Intent earth2MarsIntent = new Intent(Earth2Mars.this, Earth2Mars.class);
                startActivity(earth2MarsIntent);
//                newGame();
//                return true;
            case R.id.inputMarsDate:
                Intent mars2EarthIntent = new Intent(Earth2Mars.this, Mars2Earth.class);
                startActivity(mars2EarthIntent);
//                showHelp();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

//        return super.onOptionsItemSelected(item);
    }

    public GregorianCalendar getEarthInputDate(DatePicker earthDate) {
        int earthYear = earthDate.getYear();
        int earthMonth = earthDate.getMonth();
        int earthDoM = earthDate.getDayOfMonth();
        earthInputDate.set(earthYear, earthMonth, earthDoM);

        return earthInputDate;
    }

    protected void calcMarsDate(DatePicker earthDate){
        double jdate, ls;
        String output;
        String ls_str;

        jdate = Convert2Julian(getEarthInputDate(earthDate));
        ls = Convert2Ls(jdate);
        ls_str = ls_format.format(ls);

// year, month, ls, sol
        output = "Ls " + ls_str + " Mars Year " + marsOutputYear;
        marsDate.setText(output);

    }

    protected double Convert2Julian(GregorianCalendar earthInputDate){
        double jdate;
        int year;
        int month;
        int day;

        year = earthInputDate.get(Calendar.YEAR);
        Log.i(theTag, "Input year from earthInputDate is " + year);
        month = earthInputDate.get(Calendar.MONTH) + 1;
        Log.i(theTag, "Input mont from earthInputDate is " + month);
        day = earthInputDate.get(Calendar.DAY_OF_MONTH);
        Log.i(theTag, "Input day from earthInputDate is " + day);

        jdate= day-32075+1461*(year+4800+(month-14)/12)/4+367*(month-2-(month-14)/12*12)/12-3*((year+4900+(month-14)/12)/100)/4;
        Log.i(theTag, "1 -The jdate calculated from earthInputDate is " + jdate);

        return jdate;
    }

    protected double Convert2Ls(double jdate){
// Convert a Julian date to corresponding "sol" and "Ls"
        double sol;
        double ls;
        int martianyear;
        int martianmonth;
        String output;

        double jdate_ref = 2.442765667e6; // 19/12/1975 4:00:00, such that Ls=0
// jdate_ref is also the begining of Martian Year "12"
        int martianyear_ref = 12;
        double earthday = 86400.0;
        double marsday = 88775.245;
        double marsyear = 668.60; // number of sols in a martian year

        sol = (jdate - jdate_ref) * earthday/marsday;
        Log.i(theTag, "The calculated sol is " + sol);

        martianyear = martianyear_ref;
        Log.i(theTag, "The calculated martian year is " + martianyear);
// Compute Martian Year #, along with sol value
// sol being computed modulo the number of sols in a martian year
        while (sol >= marsyear){
            sol = sol - marsyear;
            martianyear = martianyear + 1;
        }
        while (sol < 0.0){
            sol = sol + marsyear;
            martianyear = martianyear - 1;
        }
        Log.i(theTag, "The adjusted martian year is " + martianyear);
//document.dummy.dummy1.value=sol;
        marsOutputYear = martianyear;
// convert sol number to Ls
        ls = Sol2Ls(sol);
        Log.i(theTag, "The calculated ls is "+ ls);

// Knowing Ls compute martian month
        martianmonth = 1 + (int)Math.floor(((ls))/30.);
        Log.i(theTag, "The calculated martian month is " + martianmonth);
        return ls;

    }

/*--------------------------------------------------------------------------*/

    protected double Sol2Ls(double sol) {
        double ls;

        double year_day = 668.6; // number of sols in a martian year
        double peri_day = 485.35; // perihelion date
        double e_ellip = 0.09340; // orbital ecentricity
        double timeperi = 1.90258341759902;// 2*Pi*(1-Ls(perihelion)/360); Ls(perihelion)=250.99
        double rad2deg = 180./Math.PI;

        int i;
        double zz,zanom;
        int zdx = 10;
        double zx0,zteta, xx;
        int zx0_int,xref,xx0;
// xref: mean anomaly, zx0: eccentric anomaly, zteta: true anomaly

        zz = (sol - peri_day)/year_day;
        zanom = 2. * Math.PI * (zz - Math.round(zz));
        xref = (int)Math.abs(zanom);

// Solve Kepler equation zx0 - e *sin(zx0) = xref
// Using Newton iterations
        zx0 = xref + e_ellip * Math.sin(xref);
        zx0_int = (int)zx0;
        xx = (1.-e_ellip * Math.cos(zx0_int));
        xx0 = (int)(e_ellip * Math.sin (((zx0_int) - xref)/(xx)));
        do {
            zdx = -(zx0_int - xx0);
            zx0 = zx0 + zdx;
        } while (zdx > 1.e-7);
        if (zanom < 0) zx0 = -zx0;

// Compute true anomaly zteta, now that eccentric anomaly zx0 is known
        zteta = 2. * Math.atan (Math.sqrt ((1. + e_ellip)/(1. - e_ellip)) * Math.tan (zx0/2.));

// compute Ls
        ls = zteta - timeperi;
        if(ls < 0) ls = ls + 2. * Math.PI;
        if(ls > 2. * Math.PI) ls = ls - 2. * Math.PI;
// convert Ls into degrees
        ls = rad2deg * ls;

        return ls;
    }

}
