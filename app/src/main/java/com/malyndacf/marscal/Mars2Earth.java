package com.malyndacf.marscal;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by malynda on 4/13/15.
 */
public class Mars2Earth extends ActionBarActivity {

    private double marsInputDate;
    private int marsInputYear;
    private GregorianCalendar earthOutputDate = new GregorianCalendar();

    private double solsinamarsyear=668.6; // number of sols in a martian year
    private double sollength=88775.245; // sol length, in seconds
    private double daylength=86400; // Earth day length, in seconds
    private double yearlength=365.2422; // number of earth days in an earth year

    private double ref_marsyear=26;
    private double ref_jdate=2452383.23; // Julian date for April 18.7 2002, Ls=0, beginning of Mars Year 26

    private double year_day=668.6; // number of sols in a martian year
    private double e_ellip=0.09340; // orbital eccentricity
    private double peri_day=485.35; // perihelion date (in sols)
    private double timeperi=1.90258341759902; // 2*Pi*(1-Ls(perihelion)/360); Ls(perihelion)=250.99
    private double rad2deg=180./Math.PI;

    EditText marsDate;
    EditText marsYear;
    TextView earthDate;

    String theTag = "M2E";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mars2earth);

        marsDate = (EditText)findViewById(R.id.inputMarsDate);
        marsYear = (EditText)findViewById(R.id.inputMarsYear);
        earthDate = (TextView)findViewById(R.id.earthDateOutput);

        Button calcMars2Earth = (Button) findViewById(R.id.bCalcMars2Earth);
        calcMars2Earth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                calcEarthDate();
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
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        switch (item.getItemId()) {
            case R.id.earthDateInput:
                Intent earth2MarsIntent = new Intent(Mars2Earth.this, Earth2Mars.class);

                startActivity(earth2MarsIntent);
//                newGame();
//                return true;
            case R.id.inputMarsDate:
                Intent mars2EarthIntent = new Intent(Mars2Earth.this, Mars2Earth.class);
                startActivity(mars2EarthIntent);
//                showHelp();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

//        return super.onOptionsItemSelected(item);
    }

    protected void calcEarthDate(){
        double jdate;
        String output;
        double marsDate = getMarsInputDate();
        int marsYear = getMarsInputYear();

        jdate = mars2Julian(marsDate,marsYear);

        earthOutputDate = Julian2Gregorian(jdate);

        output = ""+earthOutputDate.get(Calendar.MONTH)+"/"+earthOutputDate.get(Calendar.DAY_OF_MONTH)
                +"/"+earthOutputDate.get(Calendar.YEAR);
        earthDate.setText(output);

    }

    protected double mars2Julian(double marsDate, int marsYear){
        double sol;
        double ls;

        int marsyear; // Martian Year
        double jdate; // julian date

        ls=marsDate;
// check it is a valid value of Ls:
//        CheckGivenLs();
        Log.i(theTag, "1 -The Ls input is " + marsDate);
        marsyear=marsYear;
// check if it is a valid value
//        CheckGivenMarsYear();
        Log.i(theTag, "2 -The year input is " + marsYear);
// 1. Find julian date for the (beginning of) chose Mars Year
        jdate=(marsyear-ref_marsyear)*(solsinamarsyear*(sollength/daylength))+ref_jdate;
        Log.i(theTag, "3 -The julian date calculated is" + jdate);
// 2. Find the number of martian sols corresponding to sought Solar Longitude
        sol=Ls2Sol(ls);
        Log.i(theTag, "4 -The sol calculated is " + sol);
// small fix; for Ls=0, we get sol=668.59987 instead of sol~0
        if (sol>=668.59) {
            sol=sol+0.01-solsinamarsyear;
        }

//3. Add up these sols to get julian date
        jdate=jdate+sol*(sollength/daylength);
        Log.i(theTag, "5 -The jdate updated is " + jdate);

        return jdate;
    }

    public int getMarsInputYear() {
        marsInputYear = Integer.parseInt(marsYear.getText().toString());
//        if (marsInputYear)
//        {
//
//        marsInputYear}
        return marsInputYear;
    }

    public double getMarsInputDate() {
        marsInputDate = Double.parseDouble(marsDate.getText().toString());

        return marsInputDate;
    }

    protected double Ls2Sol (double ls){
        double sol;
        double zteta; // true anomaly
        double zx0; // eccentric anomaly
        double xref; // xref: mean anomaly

        zteta=ls/rad2deg+timeperi;
        zx0=2.0*Math.atan(Math.tan(0.5*zteta)/Math.sqrt((1.+e_ellip)/(1.-e_ellip)));
        xref=zx0-e_ellip*Math.sin(zx0);

        sol=(xref/(2.*Math.PI))*year_day+peri_day;
        return sol;
    }


    /*--------------------------------------------------------------------------*/
    protected GregorianCalendar Julian2Gregorian(double jdate) {
        double ijj,is,ir3,iap,ir2,imp,ir1,ij;
        int year, month, day;

// convert julian date to gregorian date
        ijj=Math.floor(jdate+0.5);
        is=Math.floor((4.*ijj-6884477.)/146097.);
        ir3=ijj-Math.floor((146097.*is+6884480.)/4.);
        iap=Math.floor((4.*ir3+3.)/1461.);
        ir2=ir3-Math.floor(1461.*iap/4.);
        imp=Math.floor((5.*ir2+461.)/153.);
        ir1=ir2-Math.floor((153.*imp-457.)/5.);
        ij=ir1+1;
        if (imp>=13) {
            imp=imp-12;
            iap=iap+1;
        }

        year=(int)Math.floor(iap+100*is);
        month=(int)imp;
        day=(int)ij;

        earthOutputDate.set(year, month, day);
        return earthOutputDate;
    }


}
