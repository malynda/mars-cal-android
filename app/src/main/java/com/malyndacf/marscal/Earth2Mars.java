package com.malyndacf.marscal;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by malynda on 4/14/15.
 */
public class Earth2Mars extends Activity {

    private double marsOutputDate;
    private double marsOutputYear;
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

    TextView marsDate;
    DatePicker earthDate;

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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public GregorianCalendar getEarthInputDate(DatePicker earthDate) {
        int earthYear = earthDate.getYear();
        int earthMonth = earthDate.getMonth();
        int earthDoM = earthDate.getDayOfMonth();
        earthInputDate.set(earthYear, earthMonth, earthDoM);

        return earthInputDate;
    }

    protected void calcMarsDate(DatePicker earthDate){
        double jdate;

        jdate = Convert2Julian(getEarthInputDate(earthDate));



    }

    protected double Convert2Julian(GregorianCalendar earthInputDate){
        double jdate;
        int year;
        int month;
        int day;

        year=earthInputDate.get(Calendar.YEAR);
        month=earthInputDate.get(Calendar.MONTH);
        day=earthInputDate.get(Calendar.DAY_OF_MONTH);

        jdate= day-32075+1461*(year+4800+(month-14)/12)/4+367*(month-2-(month-14)/12*12)/12-3*((year+4900+(month-14)/12)/100)/4;

        return jdate;
    }

    protected double Convert2Ls(double jdate){
// Convert a Julian date to corresponding "sol" and "Ls"
        var sol;
        var ls;
        var martianyear;
        var martianmonth;

        var jdate_ref=2.442765667e6; // 19/12/1975 4:00:00, such that Ls=0
// jdate_ref is also the begining of Martian Year "12"
        var martianyear_ref=12;
        var earthday=86400.0;
        var marsday=88775.245;
        var marsyear=668.60; // number of sols in a martian year

// Start by converting given date to Julian date
        Convert2Julian();

// Convert julian days to sol date
        jdate=document.calendar.julian.value;

        sol=(jdate-jdate_ref)*earthday/marsday;

        martianyear=martianyear_ref;
// Compute Martian Year #, along with sol value
// sol being computed modulo the number of sols in a martian year
        while (sol>=marsyear){
            sol=sol-marsyear;
            martianyear=martianyear+1;
        }
        while (sol<0.0){
            sol=sol+marsyear;
            martianyear=martianyear-1;
        }

//document.dummy.dummy1.value=sol;

// convert sol number to Ls
        ls=Sol2Ls(sol);

// Knowing Ls compute martian month
        martianmonth=1+Math.floor(ls/30.);

//Display value with a maximum of 2 decimal digits
        document.calendar.martianyear.value=martianyear;
        document.calendar.martianmonth.value=martianmonth;
        document.calendar.ls.value=Math.round(ls*10)/10;
//document.calendar.sol.value=Math.round(sol*10)/10;
        document.calendar.sol.value=1+Math.floor(sol);
    }

/*--------------------------------------------------------------------------*/

    function Sol2Ls(sol) {
        var sol;
        var ls;

        var year_day=668.6; // number of sols in a martian year
        var peri_day=485.35; // perihelion date
        var e_ellip=0.09340; // orbital ecentricity
        var timeperi=1.90258341759902 // 2*Pi*(1-Ls(perihelion)/360); Ls(perihelion)=250.99
        var rad2deg=180./Math.PI;

        var i;
        var zz,zanom,zdx=10;
        var xref,zx0,zteta;
// xref: mean anomaly, zx0: eccentric anomaly, zteta: true anomaly

        zz=(sol-peri_day)/year_day;
        zanom=2.*Math.PI*(zz-Math.round(zz));
        xref=Math.abs(zanom);

// Solve Kepler equation zx0 - e *sin(zx0) = xref
// Using Newton iterations
        zx0=xref+e_ellip*Math.sin(xref);
        do {
            zdx=-(zx0-e_ellip*Math.sin(zx0)-xref)/(1.-e_ellip*Math.cos(zx0));
            zx0=zx0+zdx;
        }while (zdx>1.e-7);
        if (zanom<0) zx0=-zx0;

// Compute true anomaly zteta, now that eccentric anomaly zx0 is known
        zteta=2.*Math.atan(Math.sqrt((1.+e_ellip)/(1.-e_ellip))*Math.tan(zx0/2.));

// compute Ls
        ls=zteta-timeperi;
        if(ls<0) ls=ls+2.*Math.PI;
        if(ls>2.*Math.PI) ls=ls-2.*Math.PI;
// convert Ls into degrees
        ls=rad2deg*ls;

        return ls;
    }


}
