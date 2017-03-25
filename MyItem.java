/*
 * MyItem.java 1.0.0 04 May 2015
 *
 * Copyright (c) School of Geography.
 * University of Leeds, Leeds, West Yorkshire, UK. LS2 9JT
 * All rights reserved.
 *
 * This code is provided under the Academic Academic Free License v. 3.0.
 * For details, please see the http://www.opensource.org/licenses/AFL-3.0.
 */

package jonny.map.mymapapp;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * The class which lat, lng and text information to be stored so that it can be instantiated into the cluster.
 */
public class MyItem implements ClusterItem {

    // Declaring the variables needed: Location (LatLng), Title of the marker and Snippet of information to be displayed
    private final LatLng mPosition;
    private  String  title;
    private  String  snippet;

    /**
     * The location of the item to be instantiated into the clusterer
     * @param lat Latitude of the item
     * @param lng Longitude of the item
     */
    public MyItem(double lat, double lng) {

        mPosition = new LatLng(lat, lng);

    }

    /**
     * Returns the google LatLng variable of the item
     * @return mPosition the LatLng of the item
     */
    public LatLng getPosition() {
        return mPosition;
    }

    /**
     * Gets the marker title.
     * @return title of the marker.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the snippet.
     * @return snippet of information to be displayed in the marker popup.
     */
    public String getSnippet() {
        return snippet;
    }

    /**
     * Sets the title of the marker.
     * @param title of the marker.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the information for the marker pop up.
     * @param snippet for the marker pop up.
     */
    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }
}

/**
 * Overrides the default cluster renderer so that marker options can be accessed.
 * Nested class as is only useful to one class.
 */
class MyClusterRenderer extends DefaultClusterRenderer<MyItem> {

    public MyClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
    }

    /**
     * Called before the cluster item is rendered to allow customisation of the items within the cluster.
     * Custom marker title, snippet and icon.
     * @param item within the cluster
     * @param markerOptions allows access to the marker options enabling customisation.
     */
    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);

        // Access to the marker options: setting the title, snippet and icon.
        markerOptions.title(item.getTitle())
                     .snippet(item.getSnippet())
                     .icon(BitmapDescriptorFactory.fromAsset("marker.png"));

    }

    @Override
    protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);


    }
}


