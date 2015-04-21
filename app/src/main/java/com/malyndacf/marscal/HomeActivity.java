package com.malyndacf.marscal;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class HomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button launchMars2Earth = (Button) findViewById(R.id.bLaunchMars2Earth);
        launchMars2Earth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent mars2EarthIntent = new Intent(HomeActivity.this, Mars2Earth.class);

                startActivity(mars2EarthIntent);

            }
        });

        Button launchEarth2Mars = (Button) findViewById(R.id.bLaunchEarth2Mars);
        launchEarth2Mars.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent earth2MarsIntent = new Intent(HomeActivity.this, Earth2Mars.class);

                startActivity(earth2MarsIntent);

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
}
