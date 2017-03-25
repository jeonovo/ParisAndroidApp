/*
 * SplashScreen.java 1.0.0 04 May 2015
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
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


/**
 * Shows an image of an espresso while loading the data from a local JSON file.
 */
public class SplashScreenActivity extends Activity {

    // Get instance of singleton class
    ParisData pd = ParisData.getInstance();

    /**
     * Method which sets up the splash screen. Gets the layout displays an image and reads the JSON data file.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Clear the ArrayList ensuring there is not duplication of data.
        pd.list.clear();

        // Execute the ASyncTask class allowing background loading of the data
        new LoadData().execute();

        // Handler has a timer, 1.5 seconds
        int SPLASH_SCREEN_DELAY = 1500;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // The intent is to go from this class to the main page.
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);

                // Start the activity defined in the intent variable
                startActivity(intent);

                // Closes this activity
                finish();
           // Delay for 1.5 seconds then start the main activity
            }
        }, SPLASH_SCREEN_DELAY);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
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
     * While the splash screen is being shown these methods run in the background by a nested class extending
     * Asynchronous Task.
     *
     * Extracts the JSON objects from the String JSON and puts them into the ArrayList for storage.
     *
     * Void because no parameters are needed nor is anything returned.
     */
    class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            // Create JSON Object
            JSONObject obj;


            // JSONObject made from the String JSON
            try {
                obj = new JSONObject(getJSON());


                JSONArray jsonArray;

                // Extract the array from JSON object called "paris"
                jsonArray = obj.getJSONArray("paris");

                // For the length of th JSON array extract the object at index i
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the JSON Object at each index point
                    JSONObject jObject = jsonArray.getJSONObject(i);

                    // Extract the necessary variables
                    String cafeName = jObject.getString("Name");
                    String address = jObject.getString("Address1");
                    double lat = jObject.getDouble("Lat");
                    double lng = jObject.getDouble("Long");
                    int id = jObject.getInt("ID");

                    // Use the extracted variables as parameters for the the method setData in the Singleton class.
                    pd.setData(cafeName, address, lat, lng, id);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // return nothing from this async class
            return null;

        }

        /**
         * Reads the JSON file containing the data and returns a String of the data.
         *
         * Code adapted from Kalola,(2014)-
         * http://www.nkdroid.com/2014/11/json-parsing-from-assets-using-gson-in-android-tutorial.html
         *
         * @return json
         */
        public String getJSON() {

            // The JSON array will be read in as a string
            String json;

            try {

                // Get the JSON file from the assests folder
                InputStream is = getAssets().open("paris_data.json");

                // Determine the size of the file
                int size = is.available();

                // byte array is the size of the file
                byte[] buffer = new byte[size];

                // Read the file to the buffer
                is.read(buffer);

                // Close the stream
                is.close();

                // Convert buffer to string
                json = new String(buffer, "UTF-8");

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            // Return the JSON array from the file as a string
            return json;


        }

    }

}
