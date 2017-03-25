/*
 * MainActivity.java 1.0.0 04 May 2015
 *
 * Copyright (c) School of Geography.
 * University of Leeds, Leeds, West Yorkshire, UK. LS2 9JT
 * All rights reserved.
 *
 * This code is provided under the Academic Academic Free License v. 3.0.
 * For details, please see the http://www.opensource.org/licenses/AFL-3.0.
 */

package jonny.map.mymapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Use activity main as the layout

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * Goes to the list of cafes.
     * Called in the xml file through android:onClick
     * @param view
     */
    public void goToList(View view){

        Intent intent = new Intent(this, ListActivity.class);

        startActivity(intent);
    }

    /**
     * Method called when the map button is pressed, goes to the
     * Defined within the MapsActivity xml layout file.
     * @param view
     */
    public void goToMap(View view){

        // Intent to go to MapsActivity from this class
        Intent intent = new Intent(this, MapsActivity.class);

        // Creating a bundle to pass information to the MapsActivity along with the intent.
        Bundle b = new Bundle();

        // Placing a String of "Main" along with the key of "Map"
        b.putString("Map", "Main");

        // Bundle the extras with the intent
        intent.putExtras(b);

        // Start the activity defined in the intent.
        startActivity(intent);

    }

    /**
     * Goes to the information page
     * @param view
     */
    public void goToInfo(View view){

        // Go from this class to the InformationActivity Class
        Intent intent = new Intent(this, InformationActivity.class);

        // Start the activity.
        startActivity(intent);
    }



}
