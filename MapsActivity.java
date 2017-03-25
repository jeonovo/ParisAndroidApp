/*
 * MapsActivity.java 1.0.0 04 May 2015
 *
 * Copyright (c) School of Geography.
 * University of Leeds, Leeds, West Yorkshire, UK. LS2 9JT
 * All rights reserved.
 *
 * This code is provided under the Academic Academic Free License v. 3.0.
 * For details, please see the http://www.opensource.org/licenses/AFL-3.0.
 */

package jonny.map.mymapapp;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {

    // Might be null if Google Play services APK is not available.
    private GoogleMap mMap;

    // Get the instance of the Singleton class
    ParisData pd = ParisData.getInstance();

    // Declare a variable for the cluster manager
    private ClusterManager<MyItem> mClusterManager;

    // ArrayList for this class is a copy of the list in the ParisData class
    ArrayList<Storage> list = pd.list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // The app loses memory data as per the app lifecycle to other apps and so the arraylist data is lost.
        // So if the arraylist where the data is stored is empty
        if (ParisData.getInstance().list.isEmpty()){

            // Let the user know the map is loading
            Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();

            // Read in the data again
            readJSON();

            // repopulate the list in this class
            list = ParisData.getInstance().list;

            // Set up the cluster again with the data
            setUpCluster();

        }

    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * Sets up the map with a cluster, zoom buttons, location button.
     */
    private void setUpMap(){

        // Create the clusters and subsequent cluster items
        setUpCluster();

        // Adding zoom in and out buttons, when using one hand pinch and zoom to zoom out difficult.
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Adds a zoom to location button
        mMap.setMyLocationEnabled(true);

            // Gets the bundled intent with the key of Map.
            String value = getIntent().getExtras().getString("Map");

                // If the bundled intent is "List" (coming from the ListActivity)
                if (value.equals("List")) {

                    // Gets the ID number of the cafe which was selected in the list
                    int id = getIntent().getExtras().getInt("OID");

                    // This id number is then passed in as a parameter for the goToCafe method
                    goToCafe(id);

                // If the string sent through is "Main" (coming from main activity)
                } else if (value.equals("Main")){

                    // Blank
                    // Map already set up with cluster being called at the start of method,
                    // an intent needed to be passed to distinguish between the list and the map button

                }
    }

    /**
     * Zooms to a cafe based on the cafe chosen in either the full cafe list or the favourites list
     * @param id The ID of the chosen cafe
     */
    public void goToCafe(int id){

        // Loop through the list of cafes
        for (int i = 0; i < list.size(); i++) {

            // Match the chosen cafe id to the id in the list
            if (list.get(i).getId() == id) {

                // Create a new LatLng based on the lat lng of the matched cafe
                LatLng chosenLatLng = new LatLng(list.get(i).getLat(), list.get(i).getLng());

                // Show the current location in Google Map
                mMap.moveCamera(CameraUpdateFactory.newLatLng(chosenLatLng));

                // Zoom in the Google Map
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

            }

        }

    }

    /**
     * Sets up the marker clusterer.
     */
    private void setUpCluster() {

        // Set the location and zoom of initial view
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(48.8610189, 2.3453862), 12));

        // Initialise the cluster manager with the context and the map
        mClusterManager = new ClusterManager<>(this, mMap);
        // Overide the default renderer by calling a new cluster renderer
        mClusterManager.setRenderer(new MyClusterRenderer(MapsActivity.this, mMap, mClusterManager));

        // Set the map listeners to respond to the clusters
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        // Calls the method which adds items to the clusterer
        addItems();

    }

    /**
     * Loops through the ArrayList adding each index item to the marker clusterer.
     */
    public void addItems() {

        for (int i = 0; i < list.size(); i++) {

            // get data from array list in
            double lat = list.get(i).getLat();
            double lng = list.get(i).getLng();

            String cafeName = list.get(i).getName();
            String address = list.get(i).getAddress();

            // Create a new Item and pass lat, lng as parameters
            MyItem item = new MyItem(lat, lng);

            // Set the title of the marker
            item.setTitle(cafeName);

            // Set the information in the pop up.
            item.setSnippet(address);

            // Add the item storing the lat, long, title and snippet to the clusterer.
            mClusterManager.addItem(item);

        }

    }

    /**
     * Reads in the local JSON file from the asset folder. Only called if the Storage ArrayList is empty
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


}
