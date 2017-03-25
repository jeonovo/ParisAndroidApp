/*
 * ListActivity.java 1.0.0 04 May 2015
 *
 * Copyright (c) School of Geography.
 * University of Leeds, Leeds, West Yorkshire, UK. LS2 9JT
 * All rights reserved.
 *
 * This code is provided under the Academic Academic Free License v. 3.0.
 * For details, please see the http://www.opensource.org/licenses/AFL-3.0.
 */

package jonny.map.mymapapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Displays the cafes in order of closest distance to user.
 * Also allows the user to click to go to cafe, and long click
 */
public class ListActivity extends ActionBarActivity implements LocationListener {

    ParisData pd = ParisData.getInstance();
    Distance dis;
    ArrayList<Storage> list = pd.list;
    ArrayList<Distance> distanceList = new ArrayList<>();
    ArrayList<Integer> favouriteList = new ArrayList<>();

    // Position in the list
    private int position;

    // Instance variables for getting user location
    private String provider;
    private Location location;
    private LocationManager locationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Get the users location
        getLocation();

        // The app loses data to other apps and so the arraylist data is lost.
        if (ParisData.getInstance().list.isEmpty()){

            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();

            // Read the JSON file and populate the storage class with the data
            readJSON();

            // Re-populate this classes list with the data
            list = ParisData.getInstance().list;

        }

        // Creates an array list of the cafes and their distance from the user
        createDistanceList();

        // Read the shared preference data
        readPreferenceData();

        // Create the List View and its items
        createList();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Switch Case to decide what to do when an Action Bar item is clicked

        switch (id){
            case R.id.action_settings:
                return true;
            case R.id.refreshbutton:
                refreshButton();
                return true;
            case R.id.faves:
                // Go to the favourites list
                Intent intent = new Intent(this, FavouriteListActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    /**
     * When the activity resumes the location is updated.
     */
    public void onResume(){
        super.onResume();

        // Get a location update
        locationManager.requestLocationUpdates(provider, 1000, 1, this);

    }

    /**
     * Creates the list displayed on this page.
     *
     * List is an adapted String array created from the name and distance to the cafe taken from the distanceList.
     */
    public void createList(){

        // Create a String array based on the size of the list
        String[] listItems = new String[distanceList.size()];

        for (int i = 0; i < distanceList.size(); i++){

            // Limit the distance value to two variables
            DecimalFormat df = new DecimalFormat("#.##");

            // Get the distance value
            double dis = distanceList.get(i).getDistance();

            // String list of cafe names and their respective distances from the user location.
            listItems[i] = distanceList.get(i).getCafe() + " - " + df.format(dis) + " km";

        }

        // Create a list adapter to show the String array in the list view
        ListAdapter theAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                listItems);

        // Create the ListView variable
        final ListView theListView = (ListView) findViewById(R.id.theListView);

        // Set the adapter to the list view
        theListView.setAdapter(theAdapter);

        // Decide what happens when a list item is clicked
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
         public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

            // The cafe id number to be passed with the intent
            int cafeID = distanceList.get(position).getId();

            // Let the user know via Toast what they have selected
            Toast.makeText(ListActivity.this, "You selected " + distanceList.get(position).getCafe(), Toast.LENGTH_SHORT).show();

            // Create the intent to start the other activity
            Intent intent = new Intent(ListActivity.this, MapsActivity.class);

            // Create a new bundle to pass information with the intent
            Bundle b = new Bundle();

            // Add the cafeID to the bundle with the key OID
            b.putInt("OID", cafeID);

            // Add the String list to the bundle, to determine which methods to run in the maps activity, with the key Map
            b.putString("Map", "List");

            // Add the bundle to the intent
            intent.putExtras(b);

            // Start the MapsActivity
            startActivity(intent);

    }
 });
       // Adding a long click listener to the items in the list
       theListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {

               // Setting the position of the item click as cannot be accessed within the subsequent dialog box
               setPosition(position);

               // Create a dialog box to ask whether user wants to save the cafe to favourite list
               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ListActivity.this);

               // Set dialog box message
               alertDialogBuilder.setMessage("Save to Favourites");

               // If the user clicks yes
               alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {

                       // Creating instance variables to make code more readable
                       String cafeName = distanceList.get(getPosition()).getCafe();
                       int id = distanceList.get(getPosition()).getId();

                       // If the list is empty add the cafe
                       if (favouriteList.isEmpty()) {

                           // Add the ID to the integer Arraylist
                           favouriteList.add(id);

                           // Show the user it has been added
                           Toast.makeText(ListActivity.this, "Added " + cafeName + " to your favourites!" , Toast.LENGTH_SHORT).show();

                           // Calls the method which saves data to shared preferences
                           saveList();

                       } else {

                           // If the list already contains the id number display a message
                           if (favouriteList.contains(id)) {

                               Toast.makeText(ListActivity.this, cafeName + " is already in your favourites!", Toast.LENGTH_SHORT).show();
                           // Otherwise...
                           } else {
                                // Add the id number to the list
                               favouriteList.add(id);

                               // Show the user it has been added
                               Toast.makeText(ListActivity.this, "Added " + cafeName + " to your favourites!" , Toast.LENGTH_SHORT).show();

                               // Save the list
                               saveList();
                           }

                       }

                   }
               });

                // If the user clicks no, nothing happens
               alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {

                   }
               });

               // Creating the dialog that was just built
               AlertDialog alertDialog = alertDialogBuilder.create();

               // Show the dialog box
               alertDialog.show();

               return false;
           }
       });

    }

    /**
     * Gets the last known location of the user from the best provider between the Network and GPS.
     */
    public void getLocation(){

        // Get the location service for the location manager.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a new criteria for the location manager
        Criteria criteria = new Criteria();

        // Set the accuracy to FINE (GPS and Network signal)
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        // Get the best provider of the location
        provider = locationManager.getBestProvider(criteria, false);

        // Get the last known location using the high accuracy and best provider.
        location = locationManager.getLastKnownLocation(provider);

        if (location != null){

            // Pass the location into location changed
            onLocationChanged(location);
        }
    }

    /**
     * Updates the location if it has changed
     * @param location of the user
     */
    public void  onLocationChanged(Location location){

        // Update the location
        this.location = location;

   }

    /**
     * Populates an ArrayList with information on Cafe distance from the user and sorts based on shortest distance.
     */
    public void createDistanceList(){

        // Loop through the list containing the original data
        for (int i = 0; i < list.size(); i++){

            // Get the Name, ID, Lat and Lng of each index item
            int id = list.get(i).getId();
            String cafe = list.get(i).getName();
            double uLat = list.get(i).getLat();
            double uLng = list.get(i).getLng();

            // Create a distance variable based on the distance between the users location and the cafe location
            double distance = getDistance(uLat, uLng);

            // Populate the Distance ArrayList with objects containing the information
            setDistance(distance, id, cafe);
        }

        // Sorts the list based on the closest distance
        Collections.sort(distanceList, new Comparator<Distance>() {

            @Override
            public int compare(Distance d1, Distance d2) {
                // If the distance is less than next distance move down an index
                if (d1.getDistance() < d2.getDistance()) {
                    return -1;

                // If the distance is greater than next distance move up an index
                } else if (d1.getDistance() > d2.getDistance()) {

                    return 1;
                }

                // If neither do not move
                return 0;
            }
        });

    }

    /**
     * Returns the distance between two Lat Lngs.
     * @param lat1
     * @param lng1
     * @return Distance from the user to the cafe
     */
    public double getDistance(double lat1, double lng1){

        // Create a new location for the cafe
        Location location1 = new Location("cafe");

        // Set the location's lat lngs based on the paramter
        location1.setLatitude(lat1);
        location1.setLongitude(lng1);

        // Return the distance in Kilometres from the user to the location of the cafe
        return (double) (location.distanceTo(location1) / 1000);
    }

    /**
     * Creates an object of the Distance class and adds it to the Distance ArrayList
     * @param distance
     * @param id
     * @param cafe
     */
    public void setDistance(double distance, int id, String cafe){

        dis = new Distance();
        dis.setDistance(distance);
        dis.setCafe(cafe);
        dis.setId(id);
        distanceList.add(dis);

    }

    /**
     * Refresh the list to manually get an updated location and thus an update on the distance between the user and cafes.
     */
    public void refreshButton(){

        distanceList.clear();
        getLocation();
        createDistanceList();
        createList();


    }

    /**
     * Get the value of position, the List View item position that has been long clicked
     * @return current value of position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Set the value of position
     * @param position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Saves the current integer ArrayList of user selected cafe ID numbers to the shared preferences
     */
    public void saveList(){

        // Get the shared preferences editor
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ListActivity.this).edit();

        // Create a new JSON array to convert to a string to save
        JSONArray jsonArray = new JSONArray();

        // Loop through the integer array list
        for (int i = 0; i < favouriteList.size(); i++) {

            JSONObject jObject;
            try {

                // Create a JSON Object with the the ID as the ID number in the list
                jObject = new JSONObject().put("ID", favouriteList.get(i));
                // Put the object in the JSON array
                jsonArray.put(jObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // If the list is not empty
        if (!favouriteList.isEmpty()){

            // Convert the JSON array to string and put it in the editor with the key "fav"
            editor.putString("fav", jsonArray.toString());


        } else {

            editor.putString("fav", null);
        }

        // Commit the list to file
        editor.commit();


    }

    /**
     * Reads the shared preference of cafe id numbers into an integer ArrayList.
     */
    public void readPreferenceData(){

        // Get the shared preferences manager
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Get the String from the saved preferences with the key "fav"
        String json = preferences.getString("fav", null);

        // If the string is not null
        if (json != null){

            try {

                // Create a new JSON array from the String
                JSONArray jsonArray = new JSONArray(json);


                // Loop through each index in the array
                for (int i = 0; i < jsonArray.length(); i++) {

                    // Extract the object from each index
                    JSONObject jObject = jsonArray.getJSONObject(i);

                    // Create an integer id from the ID in the array
                    int id = jObject.getInt("ID");

                    // Add each integer id to the favouriteList ArrayList
                    favouriteList.add(id);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads in the JSON from the assets folder.
     * Called if the Storage class ArrayList is empty.
     */
    public void readJSON(){

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


    }

    /**
     * Reads the JSON file containing the data and returns a String of the data.
     *
     * Code adapted from Kalola,(2014)-
     * http://www.nkdroid.com/2014/11/json-parsing-from-assets-using-gson-in-android-tutorial.html
     *
     * @return json
     */
    public String getJSON(){

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

    @Override
    public void onStatusChanged(final String provider, final int status, final Bundle extras) {
    }

    @Override
    public void onProviderDisabled(final String provider) {
    }

    @Override
    public void onProviderEnabled(final String provider) {
    }

}
