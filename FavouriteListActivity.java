/*
 * FavouriteListActivity.java 1.0.0 04 May 2015
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A list displaying the users saved cafes.
 *
 * List populated from reading the shared preferences populated from the full list view.
 */
public class FavouriteListActivity extends ActionBarActivity implements LocationListener {

    // Distance class instance
    Distance dis;

    // ArrayList to compare the Cafe Ids to.
    ArrayList<Storage> storage = ParisData.getInstance().list;

    // New ArrayList for the matched IDs and subsequent data.
    ArrayList<Distance> faveList = new ArrayList<>();

    // ArrayList to be read into and saved.
    ArrayList<Integer> savedIdNumbers;

    // Instance variable for the position in the list.
    private int position;

    // Location instance variables.
    private LocationManager locationManager;
    private String provider;
    private Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_activity_two);

        // Get the user location.
        getLocation();

        // Read the data from the shared preferences.
        readPreferenceData();

        // Populate the ArrayList which will be displayed.
        populateList();

        // Create and display the list.
        createList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_activity_two, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Switch Case to decide what to do based on the activity button pressed
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.refresh_fave_list:
                refreshList();
                return true;
            case R.id.clear_fave_list:
                clearFaves();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * When the activity resumes these methods run.
     */
    public void onResume(){
        super.onResume();

        // Each time the page resumes get the user location.
        locationManager.requestLocationUpdates(provider, 500, 1, this);

    }

    /**
     *
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
    @Override
    public void onLocationChanged(Location location) {

        this.location = location;

    }

    /**
     * Populates the List view items with the cafe name and its distance from the user.
     */
    public void populateList(){


        Collections.sort(savedIdNumbers);

        for (int i = 0; i < savedIdNumbers.size(); i++){

            // Get the value from the data array which is the cafe ID number.
            int value = savedIdNumbers.get(i);

            for (int j = 0; j < storage.size(); j++){

                // If the storage cafe ID number matches the ID numbers saved
                if (storage.get(j).getId() == value) {

                    String cafe = storage.get(j).getName();
                    int id = storage.get(j).getId();

                    double lat = storage.get(j).getLat();
                    double lng = storage.get(j).getLng();

                    // Getting the distance between the user and the cafe.
                    double distance = getDistance(lat, lng);

                    // Setting the distance ArrayList with the id, cafe name and distance.
                    setList(distance, id, cafe);

                }

            }

        }

        // Sorts the list based on the closest distance
        Collections.sort(faveList, new Comparator<Distance>() {

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
     * Creates the list displayed on this page.
     *
     * List is an adapted String array created from the name and distance to the cafe taken from the faveList.
     */
    public void createList(){

        // String array is the size of the
        String[] listItems  = new String[faveList.size()];

        // Loop through faveList assigning items to the String array
        for (int i = 0; i < faveList.size(); i++){

            // Limit the distance to 2 decimal places
            DecimalFormat df = new DecimalFormat("#.##");

            // Each item of the list is the Cafe name and the distance in km from the user
            listItems[i] = faveList.get(i).getCafe() + " - " + df.format(faveList.get(i).getDistance()) + " km";

        }

        // Adapt the String array to be displayed on the activity layout.
        ListAdapter theAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                listItems);

        // Create the ListView variable using the layout id faveList in the xml file
        final ListView theListView = (ListView) findViewById(R.id.faveList);

        // Set the adapter to the list view
        theListView.setAdapter(theAdapter);


        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // This is position in list need the id of selected cafe
                int listID = faveList.get(position).getId();

                // Confirm to the user their selection
                String itemPicked = "You selected " + faveList.get(position).getCafe();

                Toast.makeText(FavouriteListActivity.this, itemPicked, Toast.LENGTH_SHORT).show();

                // Because inside anonymous inner class need to state class its in.
                Intent intent = new Intent(FavouriteListActivity.this, MapsActivity.class);

                // sendLocation.putExtra("Hello", itemPicked);
                Bundle b = new Bundle();

                // Send the id number with the intent
                b.putInt("OID", listID);

                // Send a the String "List" along with the intent.
                // Helps in the maps activity to determine where the intent has come from
                // and the subsequent methods to run.
                b.putString("Map", "List");

                // Put the extras with the intent
                intent.putExtras(b);

                // Start the Maps Activity
                startActivity(intent);

            }
        });

        // Add a long click listener which brings up a dialog box asking for confirmation the user wants to
        // remove the cafe from the favourites.
        theListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {


                setPosition(position);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FavouriteListActivity.this);

                // The message on the dialog box
                alertDialogBuilder.setMessage("Remove from Favourites");

                // Set what happens when the user selects 'yes'
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {


                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        // Remove the item from the list at the selected position
                        faveList.remove(getPosition());

                        // Save the new list, overwrites the current preference data
                        saveList();

                        // With the list data changed now need to 'refresh' the activity to display the new data
                        refreshActivity();

                    }
                });

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
     * Gets the position in the list of the selected item.
     * @return position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the position in the list of the selected item.
     * @param position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Sets the variables in the ArrayList
     * @param distance
     * @param id
     * @param cafe
     */
    public void setList(double distance, int id, String cafe){

        dis = new Distance();
        dis.setDistance(distance);
        dis.setCafe(cafe);
        dis.setId(id);
        faveList.add(dis);

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
     * Saves an integer ArrayList of the Cafe ID numbers to the shared preferences.
     */
    public void saveList(){

        // Get the shared preferences editor
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(FavouriteListActivity.this).edit();

        // Create a new JSON array to convert to a string to save
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < faveList.size(); i++) {

            JSONObject jObject;

            try {

                // Create a JSON Object with the the ID as the ID number in the list
                jObject = new JSONObject().put("ID", faveList.get(i));
                // Put the object in the JSON array
                jsonArray.put(jObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        if (!faveList.isEmpty()){

            // If the list is not empty convert the JSON Array to string and put it in the editor for saving.
            editor.putString("fav", jsonArray.toString());


        } else {

            // If the list is empty put nothing in the String
            editor.putString("fav", null);
        }

        // Commit to file
        editor.commit();
    }


    /**
     * Reads the shared preference of cafe id numbers into an integer ArrayList.
     */
    public void readPreferenceData(){

        // Open the preference manager
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(FavouriteListActivity.this);

        // Get the String from the saved shared preferences
        String json = preferences.getString("fav", null);

        // Create a new ArrayList
        savedIdNumbers = new ArrayList<>();

        // If the String json is not null.
        if (json != null){

            try {

                // Create new JSON Array from the String json
                JSONArray jsonArray = new JSONArray(json);

                for (int i = 0; i < jsonArray.length(); i++) {

                    // Get the object at each index
                    JSONObject jObject = jsonArray.getJSONObject(i);

                    // Get the id number
                    int id = jObject.getInt("ID");

                    // Add the id number
                    savedIdNumbers.add(id);

                }
            } catch (JSONException e) {
                e.printStackTrace();

            }

        }

    }

    /**
     * Refresh the list to manually get an updated location and thus an update on the distance between the user and cafes.
     */
    public void refreshList(){

        faveList.clear();

        // Get the user location
        getLocation();

        // Read the data from the shared preferences
        readPreferenceData();

        // Populate the arraylist which will be displayed
        populateList();

        // Display the list
        createList();

    }



    /**
     * Closes the activity, sends an intent to itself opening the activity without animation so it 'refreshes' the page.
     */
    public void refreshActivity(){

        // Finish this activity
        finish();

        // Start the activity again by sending the intent to itself
        startActivity(getIntent());

        // Disable the activity change animation
        overridePendingTransition(0,0);

    }


    /**
     * Clears the ArrayList, saves the result and refreshes the activity page.
     */
   public void clearFaves(){
       // Clear the data
       faveList.clear();
       // Overwrite the saved data
       saveList();
       // Refresh the activity
       refreshActivity();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}


